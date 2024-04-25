FROM python:3.9

RUN pip install clip-api-service

CMD clip-api-service serve --model-name=ViT-B-32:openai
EXPOSE 3000