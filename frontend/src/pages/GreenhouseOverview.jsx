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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Card,
  Grid,
  CardContent,
  IconButton
} from '@mui/material';
import { Delete, Edit, Dashboard } from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';

const GreenhouseOverview = () => {
  const { greenhouseId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [greenhouse, setGreenhouse] = useState(null);
  const [plants, setPlants] = useState([]);
  const [plant, setPlant] = useState(null);
  const [sensors, setSensors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [sensorDialogOpen, setSensorDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);

  const [form, setForm] = useState({
    name: '',
    description: '',
    plantId: '',
  });

  const [sensorForm, setSensorForm] = useState({
    note: ''
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const headers = { Authorization: `Bearer ${user.accessToken}` };

        const [ghRes, plantsRes] = await Promise.all([
          fetch(`http://localhost:8080/greenhouses/${greenhouseId}`, { headers }),
          fetch(`http://localhost:8080/plant`, { headers }),
        ]);

        if (!ghRes.ok || !plantsRes.ok)
          throw new Error('Failed to fetch data');

        const ghData = await ghRes.json();
        const plantList = await plantsRes.json();

        setGreenhouse(ghData);
        setPlants(plantList);
        setSensors(ghData.sensors);

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

  const handleSensorFormChange = (e) => {
    setSensorForm({ ...sensorForm, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    try {
      const data = {
        name: form.name,
        description: form.description,
        plant: { id: form.plantId }
      };
      const ghRes = await fetch(`http://localhost:8080/greenhouses/${greenhouseId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(data),
      });

      if (!ghRes.ok) throw new Error('Failed to update greenhouse');
      try {
        const newGreenhouse = await ghRes.json();
        setGreenhouse(newGreenhouse);
        setPlant(plants.find(i => i.id === newGreenhouse.plantId) || null)
      } catch (err) {
        console.error(err)
      }

      toast.success('Greenhouse updated!');
    } catch (err) {
      console.error(err);
      toast.error('Update failed');
    } finally {
      setEditDialogOpen(false);
    }
  };

  const handleSensorCreate = async () => {
    try {
      const data = {
        greenhouse: { id: parseInt(greenhouseId) },
        note: sensorForm.note,
      };

      const res = await fetch(`http://localhost:8080/sensors`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error('Failed to create sensor');
      toast.success('Sensor created!');
      setSensorDialogOpen(false);
      setSensorForm({ note: '' });

      // Refresh sensor list
      const updatedSensors = await fetch(`http://localhost:8080/sensors/greenhouse/${greenhouseId}`, {
        headers: { Authorization: `Bearer ${user.accessToken}` }
      }).then(res => res.json());
      setSensors(updatedSensors);
    } catch (err) {
      console.error(err);
      toast.error('Sensor creation failed');
    }
  };

  const handleDeleteSensor = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/sensors/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.accessToken}`,
        },
      });
      if (!res.ok) throw new Error();
      toast.success(`Sensor #${id} deleted`);
      setSensors(prev => prev.filter(s => s.id !== id));
    } catch {
      toast.error('Failed to delete sensor');
    }
  }


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

      <Paper sx={{ p: 3, mb: 3 }} style={{ textAlign: "start" }}>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Box>
            <Typography variant="h5">{greenhouse.name}</Typography>
            <Typography variant="body1" color="textSecondary" sx={{ mt: 1 }}>
              {greenhouse.description}
            </Typography>
            {plant && (
              <Box
                mt={3}
                p={2}
                borderRadius={2}
                bgcolor="background.paper"
                boxShadow={1}
                border="1px solid"
                borderColor="divider"
              >
                <Typography variant="h6" fontWeight="bold" gutterBottom>
                  🌿 Plant Info
                </Typography>
                <Typography variant="h6"><strong>Name:</strong> {plant.name}</Typography>
                <Typography variant="body2">
                  🌡<strong>Temperature:</strong> {plant.minTemperature}–{plant.maxTemperature} °C
                </Typography>
                <Typography variant="body2">
                  💧 <strong>Humidity:</strong> {plant.minHumidity}–{plant.maxHumidity} %
                </Typography>
                <Typography variant="body2">
                  🌱 <strong>Moisture:</strong> {plant.minMoisture}–{plant.maxMoisture} %
                </Typography>
              </Box>
            )}
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Button
              variant="contained"
              onClick={() => setSensorDialogOpen(false) || setEditDialogOpen(true)}
              startIcon={<Edit />}
            >
              Edit
            </Button>
            <Button
              variant="contained"
              color="success"
              startIcon={<Dashboard />}
              onClick={() => navigate(`/dashboard/${greenhouseId}`)}
            >
              View Dashboard
            </Button>
          </Box>
        </Box>
      </Paper>

      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Greenhouse</DialogTitle>
        <DialogContent dividers>
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
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSave} variant="contained" color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      <Box mt={6}>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Sensors</Typography>
          <Button variant="outlined" onClick={() => setSensorDialogOpen(true)}>Add Sensor</Button>
        </Box>
        <Grid container spacing={2} mt={1}>
          {sensors.map(sensor => (
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={sensor.id}>
              <Card>
                <CardContent>
                  <Typography variant="body1" color="textPrimary">
                    Sensor #{sensor.id}
                  </Typography>
                  <Typography variant="body2" color="textSecondary" gutterBottom>
                    Note: {sensor.note}
                  </Typography>

                  <IconButton
                    color="error"
                    size="small"
                    onClick={() => handleDeleteSensor(sensor.id)}
                  >
                    <Delete />
                  </IconButton>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>

      <Dialog open={sensorDialogOpen} onClose={() => setSensorDialogOpen(false)}>
        <DialogTitle>Add New Sensor</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="Note"
            name="note"
            fullWidth
            multiline
            value={sensorForm.note}
            onChange={handleSensorFormChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSensorDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSensorCreate} variant="contained">Create</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default GreenhouseOverview;
