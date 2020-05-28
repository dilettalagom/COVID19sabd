FROM python:2.7-alpine
ENV FLASK_APP app.py
ENV FLASK_ENV development

RUN mkdir /app
COPY requirement.txt /
WORKDIR /
RUN pip install -r requirement.txt
VOLUME /app
WORKDIR /app


EXPOSE 5000

ENTRYPOINT ["flask"]
CMD ["run", "--host=0.0.0.0"]
