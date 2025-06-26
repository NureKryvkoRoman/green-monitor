import React, { useEffect, useState } from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  ReferenceLine,
} from 'recharts';
import { Box, Typography, CircularProgress, Paper, Divider } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { useParams } from 'react-router';

const Dashboard = () => {
  const { greenhouseId } = useParams();
  const { user } = useAuth();

  const [sensorData, setSensorData] = useState([]);
  const [plant, setPlant] = useState(null);
  const [loading, setLoading] = useState(true);

  const formatTimestamp = (ts) => new Date(ts).toLocaleString();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const headers = { Authorization: `Bearer ${user.accessToken}` };

        const [sensorRes, plantRes] = await Promise.all([
          fetch(`http://localhost:8080/sensor-state/greenhouse/${greenhouseId}`, { headers }),
          fetch(`http://localhost:8080/greenhouses/plant/${greenhouseId}`, { headers }),
        ]);

        if (!sensorRes.ok || !plantRes.ok) throw new Error('Failed to fetch data');

        const sensorStates = await sensorRes.json();
        const plantData = await plantRes.json();
        console.log(sensorStates)

        setPlant(plantData);

        const grouped = sensorStates.reduce((acc, entry) => {
          if (!acc[entry.sensorId]) acc[entry.sensorId] = [];
          acc[entry.sensorId].push({
            timestamp: formatTimestamp(entry.timestamp),
            temperature: entry.temperature,
            humidity: entry.humidity,
            moisture: entry.moisture,
          });
          return acc;
        }, {});

        setSensorData(grouped);
      } catch (err) {
        console.error(err);
        toast.error('Error loading dashboard data');
      } finally {
        setLoading(false);
      }
    };

    if (user?.accessToken) fetchData();
  }, [greenhouseId, user]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (!plant) {
    return (
      <Box mt={4}>
        <Typography variant="h6" color="error">
          Plant data not available.
        </Typography>
      </Box>
    );
  }

  const round2 = (num) => Math.round(num * 100) / 100;

  const getYDomain = (data, metric, minBaseline, maxBaseline) => {
    const values = data
      .map((d) => d[metric])
      .filter((v) => v !== null && v !== undefined);

    if (values.length === 0) return [round2(minBaseline), round2(maxBaseline)];

    const dataMin = Math.min(...values);
    const dataMax = Math.max(...values);

    const minVal = Math.min(dataMin, minBaseline);
    const maxVal = Math.max(dataMax, maxBaseline);

    // Add a small margin (5%)
    const range = maxVal - minVal;
    const margin = range * 0.05 || 0.1;

    const domainMin = round2(minVal - margin);
    const domainMax = round2(maxVal + margin);

    return [domainMin, domainMax];
  };

  const renderChart = (data, sensorId, metric, label, minBaseline, maxBaseline, color) => {
    const domain = getYDomain(data, metric, minBaseline, maxBaseline);

    return (
      <Paper sx={{ p: 2, mb: 4 }} key={`${sensorId}-${metric}`}>
        <Typography variant="h6" gutterBottom>
          Sensor #{sensorId} - {label}
        </Typography>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="timestamp" minTickGap={20} />
            <YAxis domain={domain} />
            <Tooltip />
            <Legend />
            <Line
              type="monotone"
              dataKey={metric}
              stroke={color}
              dot={false}
              isAnimationActive={false}
            />
            <ReferenceLine
              y={minBaseline}
              label={{ value: `Min (${minBaseline})`, position: 'insideBottomLeft', fill: '#388e3c' }}
              stroke="#388e3c"
              strokeDasharray="3 3"
            />
            <ReferenceLine
              y={maxBaseline}
              label={{ value: `Max (${maxBaseline})`, position: 'insideTopLeft', fill: '#d32f2f' }}
              stroke="#d32f2f"
              strokeDasharray="3 3"
            />
          </LineChart>
        </ResponsiveContainer>
      </Paper>
    );
  };

  return (
    <Box mt={4} px={2} maxWidth={900} mx="auto">
      <Typography variant="h4" gutterBottom>
        Sensor Dashboard
      </Typography>

      <Paper sx={{ p: 3, mb: 6 }}>
        <Typography variant="h5" gutterBottom>
          Plant: {plant.name}
        </Typography>
        <Divider sx={{ mb: 2 }} />
        <Typography>🌡 Temperature: {plant.minTemperature}°C – {plant.maxTemperature}°C</Typography>
        <Typography>💧 Humidity: {plant.minHumidity}% – {plant.maxHumidity}%</Typography>
        <Typography>🌱 Moisture: {plant.minMoisture}% – {plant.maxMoisture}%</Typography>
      </Paper>

      {Object.entries(sensorData).length === 0 && (
        <Typography>No sensor data available for this greenhouse.</Typography>
      )}

      {Object.entries(sensorData).map(([sensorId, data]) => (
        <Box key={sensorId} mb={6}>
          {renderChart(
            data,
            sensorId,
            'temperature',
            'Temperature (°C)',
            plant.minTemperature,
            plant.maxTemperature,
            '#1976d2'
          )}

          {renderChart(
            data,
            sensorId,
            'humidity',
            'Humidity (%)',
            plant.minHumidity,
            plant.maxHumidity,
            '#00a152'
          )}

          {renderChart(
            data,
            sensorId,
            'moisture',
            'Moisture (%)',
            plant.minMoisture,
            plant.maxMoisture,
            '#f57c00'
          )}
        </Box>
      ))}
    </Box>
  );
};

export default Dashboard;
