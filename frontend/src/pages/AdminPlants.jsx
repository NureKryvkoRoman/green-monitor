import React, { useEffect, useState } from 'react';
import {
  Container, Typography, Paper, Table, TableHead, TableRow, TableCell,
  TableBody, IconButton, Dialog, DialogTitle, DialogContent,
  TextField, DialogActions, Button
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { toast } from 'react-toastify';
import { useDebounce } from '../hooks/useDebounce';

const AdminPlants = () => {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search);
  const [plants, setPlants] = useState([]);
  const [editPlant, setEditPlant] = useState(null);
  const [createOpen, setCreateOpen] = useState(false);
  const [form, setForm] = useState({
    name: '',
    minTemperature: '',
    maxTemperature: '',
    minHumidity: '',
    maxHumidity: '',
    minMoisture: '',
    maxMoisture: '',
  });

  const fetchPlants = async () => {
    try {
      const res = await fetch('http://localhost:8080/plant');
      const data = await res.json();
      setPlants(data);
    } catch (err) {
      toast.error('Failed to load plants');
    }
  };

  useEffect(() => {
    fetchPlants();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this plant?')) return;
    try {
      const res = await fetch(`http://localhost:8080/plant/${id}`, {
        method: 'DELETE',
      });
      if (!res.ok) throw new Error();
      toast.success('Plant deleted');
      fetchPlants();
    } catch {
      toast.error('Failed to delete plant');
    }
  };

  const handleSave = async () => {
    const isEditing = !!editPlant;
    const url = isEditing
      ? `http://localhost:8080/plant/${editPlant.id}`
      : `http://localhost:8080/plant`;
    const method = isEditing ? 'PATCH' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      });
      if (!res.ok) throw new Error();
      toast.success(isEditing ? 'Plant updated' : 'Plant created');
      handleCloseDialog();
      fetchPlants();
    } catch {
      toast.error('Failed to save plant');
    }
  };

  const handleEdit = (plant) => {
    setEditPlant(plant);
    setForm({ ...plant });
  };

  const handleCloseDialog = () => {
    setEditPlant(null);
    setCreateOpen(false);
    setForm({
      name: '',
      minTemperature: '',
      maxTemperature: '',
      minHumidity: '',
      maxHumidity: '',
      minMoisture: '',
      maxMoisture: '',
    });
  };

  const filteredPlants = plants.filter(p =>
    p.id.toString().includes(debouncedSearch.toLowerCase()) ||
    p.name.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Manage Plants</Typography>
      <TextField
        variant='outlined'
        sx={{
          backgroundColor: 'white',
          borderRadius: 1,
        }}
        label="Search by ID or Name"
        fullWidth
        margin="normal"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <Button variant="contained" sx={{ marginBottom: "1rem", mt: 2 }} onClick={() => setCreateOpen(true)}>
        Create New Plant
      </Button>

      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Temperature (°C)</TableCell>
              <TableCell>Humidity (%)</TableCell>
              <TableCell>Moisture (%)</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredPlants.map((plant) => (
              <TableRow key={plant.id}>
                <TableCell>{plant.id}</TableCell>
                <TableCell>{plant.name}</TableCell>
                <TableCell>
                  {plant.minTemperature} – {plant.maxTemperature}
                </TableCell>
                <TableCell>
                  {plant.minHumidity} – {plant.maxHumidity}
                </TableCell>
                <TableCell>
                  {plant.minMoisture} – {plant.maxMoisture}
                </TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => handleEdit(plant)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(plant.id)}>
                    <Delete />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>

      <Dialog open={createOpen || !!editPlant} onClose={handleCloseDialog}>
        <DialogTitle>{editPlant ? 'Edit Plant' : 'Create Plant'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth margin="dense" label="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <TextField
            fullWidth margin="dense" label="Min Temperature"
            type="number"
            value={form.minTemperature}
            onChange={(e) => setForm({ ...form, minTemperature: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth margin="dense" label="Max Temperature"
            type="number"
            value={form.maxTemperature}
            onChange={(e) => setForm({ ...form, maxTemperature: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth margin="dense" label="Min Humidity"
            type="number"
            value={form.minHumidity}
            onChange={(e) => setForm({ ...form, minHumidity: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth margin="dense" label="Max Humidity"
            type="number"
            value={form.maxHumidity}
            onChange={(e) => setForm({ ...form, maxHumidity: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth margin="dense" label="Min Moisture"
            type="number"
            value={form.minMoisture}
            onChange={(e) => setForm({ ...form, minMoisture: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth margin="dense" label="Max Moisture"
            type="number"
            value={form.maxMoisture}
            onChange={(e) => setForm({ ...form, maxMoisture: parseFloat(e.target.value) })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button variant="contained" onClick={handleSave}>
            {editPlant ? 'Save Changes' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminPlants;
