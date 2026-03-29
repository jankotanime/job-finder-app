#!/usr/bin/env python3
"""Append JaCoCo LINE totals from jacocoTestReport.xml to GITHUB_STEP_SUMMARY."""
import os
import sys
import xml.etree.ElementTree as ET


def main() -> None:
    if len(sys.argv) != 2:
        print("usage: jacoco_step_summary.py <path-to-jacocoTestReport.xml>", file=sys.stderr)
        sys.exit(2)
    xml_path = sys.argv[1]
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY", "")
    if not summary_path or not os.path.isfile(xml_path):
        sys.exit(0)

    root = ET.parse(xml_path).getroot()
    line = next((c for c in root.findall("counter") if c.get("type") == "LINE"), None)
    with open(summary_path, "a", encoding="utf-8") as f:
        f.write("## Backend JaCoCo\n\n")
        if line is None:
            f.write("_No LINE counter in report._\n")
            return
        missed = int(line.get("missed", 0))
        covered = int(line.get("covered", 0))
        total = missed + covered
        pct = (100.0 * covered / total) if total else 0.0
        f.write("| | |\n|:---|---:|\n")
        f.write(f"| Line coverage | **{pct:.2f}%** |\n")
        f.write(f"| Lines covered | {covered} |\n")
        f.write(f"| Lines missed | {missed} |\n")


if __name__ == "__main__":
    main()
