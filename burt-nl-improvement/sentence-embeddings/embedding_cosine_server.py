#FIXME: I am not sure SentenceTransformer is thread-safe, need to check

import os

from sentence_transformers import SentenceTransformer, util
from flask import Flask
from flask import jsonify
from flask import request
from pathlib import Path
import json

app = Flask(__name__)

print("Loading the transformer model...")

# checking if model path exists can speed up loading the model
home_dir = str(Path.home())
model_name = 'all-MiniLM-L6-v2'
model_path = '.cache/torch/sentence_transformers/sentence-transformers_' + model_name
if os.path.exists(os.path.join(home_dir, model_path)):
    embedder = SentenceTransformer(os.path.join(home_dir, model_path))
else:
    embedder = SentenceTransformer(model_name)


def compute_cosine_multiple(query, corpus):
    corpus_embeddings = embedder.encode(corpus, convert_to_tensor=True)
    query_embedding = embedder.encode(query, convert_to_tensor=True)
    cos_scores = util.pytorch_cos_sim(query_embedding, corpus_embeddings)[0]
    return cos_scores.numpy().tolist()


@app.route('/embed_cosine_multiple/', methods=['POST'])
def embed_cosine_multiple():
    content = request.json
    cos_scores = [0.0]
    if content is not None:
        query = content["query"]
        corpus = content["corpus"]
        cos_scores = compute_cosine_multiple(query, corpus)
    return jsonify({'cos_scores': cos_scores})


if __name__ == '__main__':
    # cos_scores = compute_cosine_multiple("hola", ["Oscar", "hello"])
    # print(cos_scores)
    # print(json.dumps({'cos_scores': cos_scores}))
    app.run(host='localhost', port=9091, debug=True)
