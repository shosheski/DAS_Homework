#new.main.main.py
from filters.fetch_issuers import fetch_issuers
from filters.fill_missing_data import fill_missing_data
from filters.timer import timer_start, timer_end
from filters.check_last_date import get_last_date
from utils.log_time import log_execution_time

def execute_pipeline():
    start_time = timer_start()

    issuers = fetch_issuers()

    issuer_dates = {issuer: get_last_date(issuer) for issuer in issuers}
    print(f"Issuer dates: {issuer_dates}")

    fill_missing_data(issuer_dates)

    timer_end(start_time)

    log_execution_time(start_time)

if __name__ == '__main__':
    execute_pipeline()