from DrissionPage import ChromiumPage, ChromiumOptions
def dp配置():
    co = ChromiumOptions().set_local_port(9222)
    co.set_timeouts(base=5)
    page = ChromiumPage(addr_or_opts=co)
    return page
