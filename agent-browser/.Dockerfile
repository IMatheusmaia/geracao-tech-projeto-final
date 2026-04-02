FROM jlesage/chromium

RUN apk add --no-cache \
    python3 \
    py3-pip

WORKDIR /app

COPY requirements.txt .
RUN pip3 install --break-system-packages -r requirements.txt

COPY src/ .

EXPOSE 8000

CMD ["python3", "main.py"]