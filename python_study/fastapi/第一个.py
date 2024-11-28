import asyncio
import os
import time
from datetime import date
from typing import List, Union, Optional
import uvicorn
from fastapi import FastAPI,Request
app = FastAPI()

@app.get('/')
async def hel(request:Request):
    print('开始处理请求',request.client.host)
    await asyncio.sleep(3)
    print('等待结束成功')
    return {}

@app.get('/e')
async def hel(request:Request):
    print('开始处理请求e',request.client.host)
    await asyncio.sleep(3)
    print('等待结束成功e')
    return {}

if __name__ == "__main__":
    uvicorn.run(app, port=5000, access_log=True)
