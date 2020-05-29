FROM ubuntu:18.04
USER root

RUN apt-get update
RUN apt-get install -y sudo wget python2.7 python-pip vim
RUN pip install pandas
RUN pip install cassandra-driver
RUN pip install flask

ENV FLASK_APP app.py
ENV FLASK_ENV development

RUN mkdir /app
WORKDIR /
VOLUME /app
WORKDIR /app

EXPOSE 5000

ENTRYPOINT ["flask"]
CMD ["run", "--host=0.0.0.0"]
