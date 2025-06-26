import React, { useEffect, useState } from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';
import { Box, Typography, CircularProgress } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { useParams } from 'react-router';

const Dashboard = () => {
  const { greenhouseId } = useParams();
  const { user } = useAuth();
  const [sensorData, setSensorData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSensorStates = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/sensor-states/greenhouse/${greenhouseId}`,
          {
            headers: {
              Authorization: `Bearer ${user.accessToken}`,
            },
          }
        );

        if (!res.ok) throw new Error('Failed to fetch sensor states');

        const data = await res.json();
        console.log(data);

        // Group by sensorId
        const grouped = data.reduce((acc, item) => {
          const key = `${item.sensorId}-${item.unit}`;
          if (!acc[key]) acc[key] = [];
          acc[key].push({
            ...item,
            timestamp: new Date(item.timestamp).toLocaleString(),
          });
          return acc;
        }, {});

        setSensorData(grouped);
      } catch (err) {
        console.error(err);
        toast.error('Error loading sensor data');
      } finally {
        setLoading(false);
      }
    };

    if (user?.accessToken) fetchSensorStates();
  }, [user, greenhouseId]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box mt={4}>
      <Typography variant="h4" gutterBottom>
        Sensor Dashboard
      </Typography>
      {Object.entries(sensorData).map(([key, entries]) => {
        const mean =
          entries.reduce((acc, curr) => acc + curr.value, 0) / entries.length;

        return (
          <Box key={key} mt={4}>
            <Typography variant="h6">
              Sensor ID: {entries[0].sensorId} ({entries[0].unit})
            </Typography>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={entries}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" interval={Math.ceil(entries.length / 5)} />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="value" stroke="#8884d8" dot={false} />
              </LineChart>
            </ResponsiveContainer>
            <Typography variant="body2" mt={1}>
              Mean value: <strong>{mean.toFixed(2)}</strong>
            </Typography>
          </Box>
        );
      })}
    </Box>
  );
};

export default Dashboard;
