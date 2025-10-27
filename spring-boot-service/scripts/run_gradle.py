import os
import subprocess
import sys

def run_program(cmd_list, cwd):
    result = subprocess.run(cmd_list, cwd=cwd)
    return result.returncode

def main():
    if len(sys.argv) < 2:
        print("Usage: python run_gradle.py <gradle-task-or-args...>")
        sys.exit(1)

    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_dir = os.path.abspath(os.path.join(script_dir, os.pardir))

    gradlew_name = "gradlew.bat" if os.name == "nt" else "gradlew"
    gradlew_path = os.path.join(project_dir, gradlew_name)

    if not os.path.exists(gradlew_path):
        print(f"Gradle wrapper not found at {gradlew_path}")
        sys.exit(1)

    rc = run_program([gradlew_path, "spotlessApply"], cwd=project_dir)
    if rc != 0:
        print("spotlessApply returned non-zero exit code:", rc)

    rc = run_program([gradlew_path] + sys.argv[1:], cwd=project_dir)
    sys.exit(rc)

if __name__ == "__main__":
    main()
