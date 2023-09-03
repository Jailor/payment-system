import pickle
from flask import Flask, request, jsonify
from keras.models import load_model
from sklearn.preprocessing import StandardScaler
import numpy as np

app = Flask(__name__)
best_model = None
scaler = None
expected_token = '7377364371982521421asfa#@#$afzxc%$#5'


@app.route('/predict', methods=['POST'])
def predict():
    token = request.headers.get('X-Custom-Token')

    if token != expected_token:
        return jsonify({'error': 'Unauthorized'}), 401

    data = request.json['data']
    print(data)
    if scaler is None:
        return jsonify({'error': 'Scaler not loaded'})
    # Preprocess the data using the same scaler used during training
    scaled_data = scaler.transform(np.array(data).reshape(1, -1))
    print(scaled_data)
    prediction = best_model.predict(scaled_data)
    prediction_binary = (prediction > 0.5).astype(int)
    print(prediction_binary)
    return jsonify({'prediction': prediction_binary[0][0].tolist()})


if __name__ == '__main__':
    # Load the best model
    best_model = load_model('best_model.h5')
    print("Model loaded")
    print(best_model.summary())
    # Load the fitted scaler
    with open('scaler.pkl', 'rb') as scaler_file:
        scaler = pickle.load(scaler_file)
    print("Scaler loaded")
    app.run(host='0.0.0.0', port=5000)
