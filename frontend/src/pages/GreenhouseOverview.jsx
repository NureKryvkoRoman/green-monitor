import React, { useEffect, useState } from 'react';
import {
  Container,
  Typography,
  TextField,
  MenuItem,
  Button,
  CircularProgress,
  Box,
  Paper,
} from '@mui/material';
import { useParams } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';

const GreenhouseOverview = () => {
  const { greenhouseId } = useParams();
  const { user } = useAuth();

  const [greenhouse, setGreenhouse] = useState(null);
  const [plants, setPlants] = useState([]);
  const [plant, setPlant] = useState(null);
  const [loading, setLoading] = useState(true);

  const [form, setForm] = useState({
    name: '',
    description: '',
    plantId: '',
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const headers = { Authorization: `Bearer ${user.accessToken}` };

        const [ghRes, plantsRes] = await Promise.all([
          fetch(`http://localhost:8080/greenhouses/${greenhouseId}`, { headers }),
          fetch(`http://localhost:8080/plant`, { headers }),
        ]);

        if (!ghRes.ok || !plantsRes.ok) throw new Error('Failed to fetch data');

        const ghData = await ghRes.json();
        const plantList = await plantsRes.json();

        setGreenhouse(ghData);
        setPlants(plantList);
        setForm({
          name: ghData.name || '',
          description: ghData.description || '',
          plantId: ghData.plantId || '',
        });

        const currentPlant = plantList.find(p => p.id === ghData.plantId);
        setPlant(currentPlant);
      } catch (err) {
        console.error(err);
        toast.error('Error loading greenhouse details');
      } finally {
        setLoading(false);
      }
    };

    if (user?.accessToken) fetchData();
  }, [greenhouseId, user]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    try {
      const res = await fetch(`http://localhost:8080/greenhouses/${greenhouseId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(form),
      });

      if (!res.ok) throw new Error('Failed to update greenhouse');
      toast.success('Greenhouse updated!');
    } catch (err) {
      console.error(err);
      toast.error('Update failed');
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (!greenhouse) return null;

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Greenhouse Overview
      </Typography>

      <Paper sx={{ p: 3 }}>
        <TextField
          fullWidth
          label="Name"
          name="name"
          value={form.name}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />

        <TextField
          fullWidth
          multiline
          label="Description"
          name="description"
          value={form.description}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />

        <TextField
          fullWidth
          select
          label="Plant"
          name="plantId"
          value={form.plantId}
          onChange={handleChange}
          sx={{ mb: 2 }}
        >
          {plants.map((p) => (
            <MenuItem key={p.id} value={p.id}>
              {p.name}
            </MenuItem>
          ))}
        </TextField>

        <Button variant="contained" color="primary" onClick={handleSave}>
          Save Changes
        </Button>
      </Paper>

      {plant && (
        <Box mt={4}>
          <Typography variant="h6">Selected Plant Info</Typography>
          <Typography>🌡 Temperature: {plant.minTemperature} – {plant.maxTemperature} °C</Typography>
          <Typography>💧 Humidity: {plant.minHumidity} – {plant.maxHumidity} %</Typography>
          <Typography>🌱 Moisture: {plant.minMoisture} – {plant.maxMoisture} %</Typography>
        </Box>
      )}
    </Container>
  );
};

export default GreenhouseOverview;
