#FROM python:2.7-alpine
#RUN apk update
#RUN apk add py-pip
#RUN pip3 install pandas
#RUN pip3 install cassandra-driver
#RUN pip3 install json
#RUN pip3 install flask

#FROM python:3.7.2-alpine3.9
FROM ubuntu:18.04
USER root

RUN apt-get update
RUN apt-get install -y sudo wget python2.7 python-pip vim
RUN pip install pandas
RUN pip install cassandra-driver
RUN pip install flask

#
#RUN apk add --no-cache python3-dev libstdc++ && \
#    apk add --no-cache g++ && \
#    ln -s /usr/include/locale.h /usr/include/xlocale.h && \
#    pip3 install numpy && \
#    pip3 install pandas && \ pip3 install flask
#

ENV FLASK_APP app.py
ENV FLASK_ENV development

RUN mkdir /app
#COPY requirement.txt /
WORKDIR /
#RUN pip install -r requirement.txt
VOLUME /app
WORKDIR /app


EXPOSE 5000

ENTRYPOINT ["flask"]
CMD ["run", "--host=0.0.0.0"]
