#!/usr/bin/env python3
"""Run the three Maven profiles and save their measurements in one CSV."""

import csv
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path
from tempfile import NamedTemporaryFile

ROOT = Path(__file__).resolve().parent
TARGET = ROOT / 'target'
RESULTS = ROOT / 'results' / 'results.csv'
SUITES = ('basic', 'coverage', 'mutation')


def coverage_counter(report, kind):
    counter = report.find(f"counter[@type='{kind}']")
    if counter is None:
        raise ValueError(f'JaCoCo report is missing the {kind} counter')
    covered = int(counter.get('covered'))
    return covered, covered + int(counter.get('missed'))


def percent(count, total):
    return round(100 * count / total, 1) if total else 0.0


def run_suite(maven, suite):
    print(f'Running {suite}…', flush=True)
    start = time.perf_counter()
    process = subprocess.run(
        [maven, '-B', f'-P{suite}', 'clean', 'verify',
         'org.pitest:pitest-maven:mutationCoverage'],
        cwd=ROOT, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        text=True, errors='replace',
    )
    duration = round(time.perf_counter() - start, 1)
    if process.returncode:
        print('\n'.join(process.stdout.splitlines()[-35:]), file=sys.stderr)
        raise RuntimeError(f'Maven failed for the {suite} suite (exit {process.returncode})')

    jacoco = ET.parse(TARGET / 'site' / 'jacoco' / 'jacoco.xml').getroot()
    lines, total_lines = coverage_counter(jacoco, 'LINE')
    branches, total_branches = coverage_counter(jacoco, 'BRANCH')

    mutations = ET.parse(TARGET / 'pit-reports' / 'mutations.xml').findall('mutation')
    if not mutations:
        raise ValueError(f'PIT reported no mutations for the {suite} suite')
    statuses = Counter(mutation.get('status') for mutation in mutations)
    detected = sum(mutation.get('detected') == 'true' for mutation in mutations)

    reports = list((TARGET / 'surefire-reports').glob('TEST-*.xml'))
    if len(reports) != 1:
        raise ValueError(f'Expected one Surefire report for {suite}, found {len(reports)}')
    junit = ET.parse(reports[0]).getroot()

    line_percent = percent(lines, total_lines)
    branch_percent = percent(branches, total_branches)
    print(
        f'{suite.title():8}  line {line_percent:5.1f}%  branch {branch_percent:5.1f}%  '
        f'killed {statuses["KILLED"]:2}  survived {statuses["SURVIVED"]:2}',
        flush=True,
    )
    return {
        'suite': suite,
        'junit_tests': int(junit.get('tests')),
        'lines_covered': lines,
        'lines_total': total_lines,
        'line_coverage_percent': line_percent,
        'branches_covered': branches,
        'branches_total': total_branches,
        'branch_coverage_percent': branch_percent,
        'mutants_generated': len(mutations),
        'mutants_detected': detected,
        'mutants_killed': statuses['KILLED'],
        'mutants_survived': statuses['SURVIVED'],
        'mutants_no_coverage': statuses['NO_COVERAGE'],
        'mutants_timed_out': statuses['TIMED_OUT'],
        'mutation_detection_percent': percent(detected, len(mutations)),
        'end_to_end_seconds': duration,
    }


def main():
    maven = shutil.which('mvn') or shutil.which('mvn.cmd')
    if not maven:
        sys.exit('Maven (mvn) not found. Install Maven and try again.')

    rows = [run_suite(maven, suite) for suite in SUITES]
    RESULTS.parent.mkdir(exist_ok=True)
    with NamedTemporaryFile(
        mode='w', encoding='utf-8', newline='', dir=RESULTS.parent, delete=False
    ) as output:
        pending = Path(output.name)
        try:
            writer = csv.DictWriter(output, fieldnames=list(rows[0]))
            writer.writeheader()
            writer.writerows(rows)
            output.close()
            pending.replace(RESULTS)
        finally:
            pending.unlink(missing_ok=True)
    print(f'Saved {RESULTS.relative_to(ROOT)}')


if __name__ == '__main__':
    try:
        main()
    except (OSError, ValueError, RuntimeError, ET.ParseError) as exc:
        sys.exit(f'ERROR: {exc}')
