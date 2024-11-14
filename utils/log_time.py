# utils/log_time.py
import time
import os

def log_execution_time(start_time, log_file='execution_time.txt'):
    project_root = os.path.dirname(os.path.abspath(__file__))
    log_file_path = os.path.join(project_root, '..', log_file)

    end_time = time.time()
    execution_time = end_time - start_time

    minutes = int(execution_time // 60)
    seconds = int(execution_time % 60)
    milliseconds = int((execution_time * 1000) % 1000)

    formatted_time = f"{minutes}:{seconds:02d}:{milliseconds:03d}"

    with open(log_file_path, 'a') as file:
        file.write(f"Execution Time: {formatted_time}\n")

    print(f"Execution Time: {formatted_time}")