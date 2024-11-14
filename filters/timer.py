#filters.timer.py
import time

def timer_start():
    return time.time()

def timer_end(start_time):
    print(f"Pipeline executed in {time.time() - start_time:.2f} seconds")
