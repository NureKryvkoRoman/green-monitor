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
  IconButton
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
  const [newGreenhouse, setNewGreenhouse] = useState({
    name: '',
    latitude: '',
    longitude: ''
  });

  const navigate = useNavigate();

  const handleCreateFieldChange = (e) => {
    setNewGreenhouse(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/api/greenhouses/${id}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${user.accessToken}`,
          }
        });
      console.log(res)
      if (!res.ok) {
        toast.error("Failed to delete greenhouse");
      } else {
        toast.success("Successfully deleted greenhouse");
        setGreenhouses(greenhouses.filter(gh => gh.id != id)); // remove deleted greenhouse from view
      }
    } catch (err) {
      toast.error("Failed to delete greenhouse");
      console.error(err);
    }
  };

  const handleCreateGreenhouse = async () => {
    try {
      const data = {
        ...newGreenhouse,
        latitude: parseFloat(newGreenhouse.latitude),
        longitude: parseFloat(newGreenhouse.longitude),
        user: { id: parseInt(user.id) }
      };

      console.log(data);
      const res = await fetch('http://localhost:8080/api/greenhouses', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.accessToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });
      console.log(res);

      if (!res.ok) throw new Error('Failed to create greenhouse');
      toast.success('Greenhouse created');
      setCreateDialogOpen(false);
      setNewGreenhouse({ name: '', latitude: '', longitude: '' });
      fetchGreenhouses();
    } catch (err) {
      console.error(err);
      toast.error('Error creating greenhouse');
    }
  };

  const fetchGreenhouses = async () => {
    setLoading(true);
    try {
      const res = await fetch(
        `http://localhost:8080/api/greenhouses/summary/my`,
        {
          headers: {
            Authorization: `Bearer ${user.accessToken}`
          }
        }
      );

      if (!res.ok) throw new Error('Failed to fetch greenhouses');
      const data = await res.json();
      console.log(data)
      setGreenhouses(data);
    } catch (err) {
      console.error(err);
      toast.error('Error loading greenhouses.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      navigate('/admin');
      return;
    }

    if (user?.email) fetchGreenhouses();
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
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={gh.id}>
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
                      Created: {gh.createdAt ? new Date(gh.createdAt).toLocaleString() : 'no data'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Latitude: {gh.latitude}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Longitude: {gh.longitude}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Sensors: {gh.sensorCount}
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
            label="Latitude"
            name="latitude"
            type="number"
            value={newGreenhouse.latitude}
            onChange={handleCreateFieldChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Longitude"
            name="longitude"
            type="number"
            value={newGreenhouse.longitude}
            onChange={handleCreateFieldChange}
            fullWidth
          />
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
