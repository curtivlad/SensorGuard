import numpy as np
from typing import Dict, List
from collections import deque

class AnomalyDetector:
    """
    Improved anomaly detector using Z-score method with robust statistics.
    Only adds NORMAL values to history to prevent anomaly contamination.
    """

    def __init__(self, window_size: int = 50, threshold: float = 2.5):
        """
        Args:
            window_size: Number of NORMAL values to maintain in history
            threshold: Z-score threshold for anomaly detection
        """
        self.window_size = window_size
        self.threshold = threshold
        # Store only NORMAL values for clean statistics
        self.sensor_data: Dict[str, deque] = {}
        # Track total readings (including anomalies)
        self.total_readings: Dict[str, int] = {}

    def detect(self, sensor_id: str, value: float, sensor_type: str) -> Dict:
        """
        Detect if a value is anomalous using robust statistics.

        Args:
            sensor_id: Unique sensor identifier
            value: Current sensor reading
            sensor_type: Type of sensor (TEMPERATURE, VIBRATION, etc.)

        Returns:
            Dictionary with detection results
        """
        # Initialize sensor data if first reading
        if sensor_id not in self.sensor_data:
            self.sensor_data[sensor_id] = deque(maxlen=self.window_size)
            self.total_readings[sensor_id] = 0

        self.total_readings[sensor_id] += 1

        # Get historical NORMAL data
        history = list(self.sensor_data[sensor_id])

        # Need at least 3 NORMAL values for statistical analysis
        if len(history) < 3:
            # Add to history as normal (insufficient data to judge)
            self.sensor_data[sensor_id].append(value)
            return {
                "is_anomaly": False,
                "anomaly_score": 0.0,
                "message": f"Insufficient data for anomaly detection (have {len(history)}, need 3)",
                "z_score": 0.0,
                "mean": 0.0,
                "std": 0.0,
                "history_size": len(history)
            }

        # Calculate robust statistics using ONLY normal values
        mean = float(np.mean(history))
        std = float(np.std(history))

        # Avoid division by zero
        if std == 0 or std < 0.001:
            # If std is very small, use MAD (Median Absolute Deviation) as fallback
            median = float(np.median(history))
            mad = float(np.median(np.abs(np.array(history) - median)))

            if mad < 0.001:
                # No variation at all - use simple threshold
                z_score = abs(value - mean) / 0.1  # Small denominator
            else:
                # MAD-based z-score (more robust)
                z_score = abs(value - median) / (1.4826 * mad)  # 1.4826 makes MAD comparable to std
        else:
            # Standard z-score calculation
            z_score = abs((value - mean) / std)

        # Normalize to 0-1 range for anomaly_score
        anomaly_score = min(z_score / (self.threshold * 2), 1.0)

        # Determine if anomaly
        is_anomaly = z_score > self.threshold

        # CRITICAL: Only add NORMAL values to history!
        if not is_anomaly:
            self.sensor_data[sensor_id].append(value)
        else:
            # Log that we're NOT adding this anomaly to history
            pass  # Anomaly detected - DO NOT contaminate the clean history!

        # Generate detailed message
        if is_anomaly:
            message = (
                f"⚠️ ANOMALY DETECTED! Value {value:.2f} deviates significantly "
                f"from normal range [μ={mean:.2f}, σ={std:.2f}]. "
                f"Z-score: {z_score:.2f} (threshold: {self.threshold})"
            )
        else:
            message = f"✓ Normal reading. Z-score: {z_score:.2f}, within expected range"

        return {
            "is_anomaly": bool(is_anomaly),
            "anomaly_score": float(round(anomaly_score, 3)),
            "message": message,
            "z_score": float(round(z_score, 2)),
            "mean": float(round(mean, 2)),
            "std": float(round(std, 2)),
            "history_size": len(history),
            "total_readings": self.total_readings[sensor_id]
        }

    def get_sensor_stats(self, sensor_id: str) -> Dict:
        """Get statistics for a specific sensor (based on NORMAL values only)."""
        if sensor_id not in self.sensor_data or len(self.sensor_data[sensor_id]) == 0:
            return {"error": "No data for sensor"}

        history = list(self.sensor_data[sensor_id])
        return {
            "sensor_id": sensor_id,
            "normal_values_count": len(history),
            "total_readings": self.total_readings.get(sensor_id, 0),
            "mean": float(round(np.mean(history), 2)),
            "std": float(round(np.std(history), 2)),
            "median": float(round(np.median(history), 2)),
            "min": float(round(min(history), 2)),
            "max": float(round(max(history), 2)),
            "percentile_25": float(round(np.percentile(history, 25), 2)),
            "percentile_75": float(round(np.percentile(history, 75), 2))
        }

    def reset_sensor(self, sensor_id: str) -> bool:
        """Reset history for a specific sensor."""
        if sensor_id in self.sensor_data:
            self.sensor_data[sensor_id].clear()
            self.total_readings[sensor_id] = 0
            return True
        return False