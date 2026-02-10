import numpy as np
from typing import Dict, List

class AnomalyDetector:
    """
    Simple anomaly detector using Z-score method.
    Maintains a rolling window of historical values per sensor.
    """

    def __init__(self, window_size: int = 50, threshold: float = 2.5):
        """
        Args:
            window_size: Number of historical values to maintain
            threshold: Z-score threshold for anomaly detection
        """
        self.window_size = window_size
        self.threshold = threshold
        self.sensor_data: Dict[str, List[float]] = {}

    def detect(self, sensor_id: str, value: float, sensor_type: str) -> Dict:
        """
        Detect if a value is anomalous.

        Args:
            sensor_id: Unique sensor identifier
            value: Current sensor reading
            sensor_type: Type of sensor (TEMPERATURE, VIBRATION, etc.)

        Returns:
            Dictionary with detection results
        """
        # Initialize sensor data if first reading
        if sensor_id not in self.sensor_data:
            self.sensor_data[sensor_id] = []

        # Get historical data
        history = self.sensor_data[sensor_id]

        # Need at least 3 values for statistical analysis
        if len(history) < 3:
            self.sensor_data[sensor_id].append(value)
            return {
                "is_anomaly": False,
                "anomaly_score": 0.0,
                "message": "Insufficient data for anomaly detection"
            }

        # Calculate Z-score
        mean = np.mean(history)
        std = np.std(history)

        # Avoid division by zero
        if std == 0:
            z_score = 0
        else:
            z_score = abs((value - mean) / std)

        # Normalize to 0-1 range for anomaly_score
        anomaly_score = min(z_score / (self.threshold * 2), 1.0)

        # Determine if anomaly
        is_anomaly = z_score > self.threshold

        # Update history (maintain rolling window)
        self.sensor_data[sensor_id].append(value)
        if len(self.sensor_data[sensor_id]) > self.window_size:
            self.sensor_data[sensor_id].pop(0)

        # Generate message
        if is_anomaly:
            message = (
                f"Anomaly detected! Value {value:.2f} deviates significantly "
                f"from mean {mean:.2f} (±{std:.2f}). Z-score: {z_score:.2f}"
            )
        else:
            message = f"Normal reading. Z-score: {z_score:.2f}"

        return {
            "is_anomaly": bool(is_anomaly),
            "anomaly_score": float(round(anomaly_score, 3)),
            "message": message,
            "z_score": float(round(z_score, 2)),
            "mean": float(round(mean, 2)),
            "std": float(round(std, 2))
        }

    def get_sensor_stats(self, sensor_id: str) -> Dict:
        """Get statistics for a specific sensor."""
        if sensor_id not in self.sensor_data or len(self.sensor_data[sensor_id]) == 0:
            return {"error": "No data for sensor"}

        history = self.sensor_data[sensor_id]
        return {
            "sensor_id": sensor_id,
            "sample_count": len(history),
            "mean": round(np.mean(history), 2),
            "std": round(np.std(history), 2),
            "min": round(min(history), 2),
            "max": round(max(history), 2)
        }