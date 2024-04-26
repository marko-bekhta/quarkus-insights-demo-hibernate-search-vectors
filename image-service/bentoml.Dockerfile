FROM       quay.io/fedora/fedora:37

RUN        dnf -y update && dnf -y install git python3-pip

RUN        groupadd -r dev && useradd  -g dev -u 1000 dev
RUN        mkdir -p /home/dev/
RUN        chown -R dev:dev /home/dev

# From here we run everything as dev user
USER dev

ENV        HOME /home/dev
ENV        PATH $HOME/bin:$HOME/.local/bin:$PATH

WORKDIR    /home/dev/
RUN git clone https://github.com/bentoml/BentoClip.git
RUN pip install -r /home/dev/BentoClip/requirements.txt

COPY --chown=dev:dev service.py /home/dev/service.py

EXPOSE     3000

CMD ["bash"]
# bentoml serve service:clip