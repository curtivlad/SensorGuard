from flask import Flask, request, jsonify
from flask_cors import CORS
from detector import AnomalyDetector
import logging

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Initialize anomaly detector
detector = AnomalyDetector(window_size=50, threshold=2.5)

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint."""
    return jsonify({
        "status": "UP",
        "service": "anomaly-detector",
        "version": "1.0.0"
    }), 200

@app.route('/detect', methods=['POST'])
def detect_anomaly():
    """
    Detect anomaly in sensor reading.

    Expected JSON body:
    {
        "sensorId": "TEMP-001",
        "value": 25.5,
        "sensorType": "TEMPERATURE"
    }
    """
    try:
        data = request.get_json()

        # Validate input
        if not data:
            return jsonify({"error": "No data provided"}), 400

        sensor_id = data.get('sensorId')
        value = data.get('value')
        sensor_type = data.get('sensorType', 'UNKNOWN')

        if not sensor_id or value is None:
            return jsonify({"error": "sensorId and value are required"}), 400

        # Convert value to float
        try:
            value = float(value)
        except (ValueError, TypeError):
            return jsonify({"error": "value must be a number"}), 400

        logger.info(f"Detecting anomaly for sensor {sensor_id}, value: {value}")

        # Perform detection
        result = detector.detect(sensor_id, value, sensor_type)

        # Format response for Spring Boot Gateway
        response = {
            "isAnomaly": result["is_anomaly"],
            "anomalyScore": result["anomaly_score"],
            "message": result["message"]
        }

        logger.info(f"Detection result: {response}")

        return jsonify(response), 200

    except Exception as e:
        logger.error(f"Error in anomaly detection: {str(e)}")
        return jsonify({
            "isAnomaly": False,
            "anomalyScore": 0.0,
            "message": f"Error: {str(e)}"
        }), 500

@app.route('/stats/<sensor_id>', methods=['GET'])
def get_stats(sensor_id):
    """Get statistics for a specific sensor."""
    try:
        stats = detector.get_sensor_stats(sensor_id)
        return jsonify(stats), 200
    except Exception as e:
        logger.error(f"Error getting stats: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/stats', methods=['GET'])
def get_all_stats():
    """Get statistics for all sensors."""
    try:
        all_stats = {}
        for sensor_id in detector.sensor_data.keys():
            all_stats[sensor_id] = detector.get_sensor_stats(sensor_id)
        return jsonify(all_stats), 200
    except Exception as e:
        logger.error(f"Error getting all stats: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    logger.info("Starting Anomaly Detector Service...")
    app.run(host='0.0.0.0', port=5000, debug=True)