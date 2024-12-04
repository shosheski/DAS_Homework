from concurrent.futures import ThreadPoolExecutor
from Homework1.filters.fetch_issuers import fetch_issuers
from Homework1.filters.fill_missing_data import fill_missing_data
from Homework1.filters.timer import timer_start, timer_end
from Homework1.filters.check_last_date import get_last_checked_date
from Homework1.utils.log_time import log_execution_time

def fetch_data(issuer):
    last_checked_date = get_last_checked_date()
    fill_missing_data(issuer, last_checked_date)

def starter():
    start_time = timer_start()

    issuers = fetch_issuers()
    print(f"Fetched issuers: {issuers}")

    with ThreadPoolExecutor(max_workers=64) as executor:
        executor.map(fetch_data, issuers)

    timer_end(start_time)
    log_execution_time(start_time)

if __name__ == '__main__':
    starter()