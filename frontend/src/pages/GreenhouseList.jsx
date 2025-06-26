import React, { useEffect, useState } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Container,
  CircularProgress,
  Box,
  Button,
  Dialog,
  TextField,
  DialogTitle,
  DialogActions,
  DialogContent,
  IconButton,
  MenuItem,
  Select,
  InputLabel,
  FormControl
} from '@mui/material';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import { Link, useNavigate } from 'react-router';
import { Delete } from '@mui/icons-material';

const GreenhouseList = () => {
  const { user } = useAuth();
  const [greenhouses, setGreenhouses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [plants, setPlants] = useState([]);
  const [newGreenhouse, setNewGreenhouse] = useState({
    name: '',
    description: '',
    plantId: ''
  });

  const navigate = useNavigate();

  const handleCreateFieldChange = (e) => {
    setNewGreenhouse(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/greenhouses/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.accessToken}`,
        }
      });
      if (!res.ok) {
        toast.error("Failed to delete greenhouse");
      } else {
        toast.success("Successfully deleted greenhouse");
        setGreenhouses(greenhouses.filter(gh => gh.id !== id));
      }
    } catch (err) {
      toast.error("Failed to delete greenhouse");
      console.error(err);
    }
  };

  const handleCreateGreenhouse = async () => {
    try {
      const data = {
        name: newGreenhouse.name,
        description: newGreenhouse.description,
        plant: { id: parseInt(newGreenhouse.plantId) }
      };

      console.log(data)
      const res = await fetch('http://localhost:8080/greenhouses', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.accessToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (!res.ok) throw new Error('Failed to create greenhouse');
      toast.success('Greenhouse created');
      setCreateDialogOpen(false);
      setNewGreenhouse({ name: '', description: '', plantId: '' });
      fetchGreenhouses();
    } catch (err) {
      console.error(err);
      toast.error('Error creating greenhouse');
    }
  };

  const fetchGreenhouses = async () => {
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/greenhouses/summary/my', {
        headers: {
          Authorization: `Bearer ${user.accessToken}`
        }
      });

      if (!res.ok) throw new Error('Failed to fetch greenhouses');
      const data = await res.json();
      setGreenhouses(data);
    } catch (err) {
      console.error(err);
      toast.error('Error loading greenhouses.');
    } finally {
      setLoading(false);
    }
  };

  const fetchPlants = async () => {
    try {
      const res = await fetch('http://localhost:8080/plant');
      if (!res.ok) throw new Error('Failed to fetch plants');
      const data = await res.json();
      setPlants(data);
    } catch (err) {
      console.error(err);
      toast.error('Failed to load plant list');
    }
  };

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      navigate('/admin');
      return;
    }

    if (user?.email) {
      fetchGreenhouses();
      fetchPlants();
    }
  }, [user]);

  if (!user) return (
    <Typography variant="h4" marginTop={'2rem'}>
      Welcome to GreenMonitor! <i><Link to={'/login'}>Login</Link></i> or <i><Link to={'/register'}>Register</Link></i> to work with the website.
    </Typography>
  );

  if (user?.role === 'ADMIN') return null;

  return (
    <Container maxWidth="lg" sx={{ mt: 6 }}>
      <Typography variant="h4" gutterBottom>
        My Greenhouses
      </Typography>

      <Box display="flex" justifyContent="flex-end" mb={2}>
        <Button variant="contained" onClick={() => setCreateDialogOpen(true)}>
          + Add Greenhouse
        </Button>
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" mt={4}>
          <CircularProgress />
        </Box>
      ) : greenhouses.length === 0 ? (
        <Typography variant="body1">You don’t have any greenhouses yet.</Typography>
      ) : (
        <Grid container spacing={3} mt={2}>
          {greenhouses.map((gh) => (
            <Grid size={{ xs: 12, sm: 6, md: 4, }} key={gh.id}>
              <Card
                sx={{ height: '100%' }}
                onClick={() => navigate(`/greenhouses/${gh.id}`)}
                style={{ cursor: 'pointer' }}
              >
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {gh.name || `Greenhouse #${gh.id}`}
                  </Typography>
                  <Box textAlign="left">
                    <Typography variant="body2" color="text.secondary">
                      Description: {gh.description || 'No description'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Plant ID: {plants.find(p => p.id === gh.plantId)?.name || gh.plantId}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Sensors: {gh.sensors?.length || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Notifications: {gh.notifications?.length || 0}
                    </Typography>
                  </Box>
                  <IconButton
                    color="error"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(gh.id);
                    }}
                    sx={{ mt: 1 }}
                  >
                    <Delete />
                  </IconButton>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)}>
        <DialogTitle>Create New Greenhouse</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Greenhouse Name"
            name="name"
            value={newGreenhouse.name}
            onChange={handleCreateFieldChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Description"
            name="description"
            value={newGreenhouse.description}
            onChange={handleCreateFieldChange}
            fullWidth
          />
          <FormControl fullWidth margin="dense">
            <InputLabel>Plant</InputLabel>
            <Select
              name="plantId"
              value={newGreenhouse.plantId}
              label="Plant"
              onChange={handleCreateFieldChange}
            >
              {plants.map((plant) => (
                <MenuItem key={plant.id} value={plant.id}>
                  {plant.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCreateGreenhouse} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default GreenhouseList;
