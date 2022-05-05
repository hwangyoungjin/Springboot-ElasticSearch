FROM docker.elastic.co/elasticsearch/elasticsearch:7.10.0
WORKDIR /usr/share/elasticsearch
# https://esbook.kimjmin.net/06-text-analysis/6.7-stemming/6.7.2-nori
RUN ./bin/elasticsearch-plugin install analysis-nori