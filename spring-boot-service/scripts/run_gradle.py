import os
import subprocess
import sys


def run_program(
    program: str | os.PathLike, arguments: list[str] | None = None, cwd: str | None = None
) -> int:
    args = arguments or []
    result = subprocess.run([program] + args, cwd=cwd)
    return result.returncode


def main():
    if len(sys.argv) < 2:
        print("Usage: python run_gradle.py <task>")
        sys.exit(1)

    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_dir = os.path.abspath(os.path.join(script_dir, ".."))
    gradlew = "gradlew.bat" if os.name == "nt" else "gradlew"
    gradlew_path = os.path.join(project_dir, gradlew)

    run_program(gradlew_path, ["spotlessApply"], cwd=project_dir)
    return_code = run_program(gradlew_path, sys.argv[1:], cwd=project_dir)

    sys.exit(return_code)


if __name__ == "__main__":
    main()