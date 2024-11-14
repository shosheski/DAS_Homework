from filters.fetch_issuers import fetch_issuers
from filters.fill_missing_data import fill_missing_data
from filters.timer import timer_start, timer_end
from filters.check_last_date import get_last_checked_date
from utils.log_time import log_execution_time

def starter():
    start_time = timer_start()

    issuers = fetch_issuers()

    issuer_dates = {issuer: get_last_checked_date() for issuer in issuers}
    print(f"Issuer dates: {issuer_dates}")

    for issuer, last_checked_date in issuer_dates.items():
        fill_missing_data(issuer, last_checked_date)

    timer_end(start_time)
    log_execution_time(start_time)

if __name__ == '__main__':
    starter()