import React, { useEffect, useState } from 'react';
import {
  Container, Typography, Paper, Table, TableHead, TableRow, TableCell, TableBody,
  IconButton, Dialog, DialogTitle, DialogContent, TextField, DialogActions, Button,
  Select, MenuItem
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router';
import { useDebounce } from '../hooks/useDebounce';

const AdminDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search);
  const [users, setUsers] = useState([]);
  const [editUser, setEditUser] = useState(null);
  const [formData, setFormData] = useState({ email: '', role: 'USER' });
  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState({ email: '', password: '', role: 'USER' });

  const fetchUsers = async () => {
    try {
      const res = await fetch('http://localhost:8080/user', {
        headers: { Authorization: `Bearer ${user.accessToken}` },
      });
      if (!res.ok) throw new Error('Failed to load users');
      const data = await res.json();
      setUsers(data);
    } catch (err) {
      toast.error('Error fetching users');
    }
  };

  useEffect(() => {
    if (user?.role !== 'ADMIN') navigate('/login');
    fetchUsers();
  }, [user]);

  const isEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const handleCreateUser = async () => {
    if (!isEmail(createForm.email)) return toast.error('Please enter a valid email');

    try {
      const res = await fetch('http://localhost:8080/user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(createForm),
      });
      if (!res.ok) throw new Error();
      toast.success('User created');
      setCreateOpen(false);
      setCreateForm({ email: '', password: '', role: 'USER' });
      fetchUsers();
    } catch {
      toast.error('Error creating user');
    }
  };

  const handleUpdateUser = async () => {
    if (!isEmail(formData.email)) return toast.error('Please enter a valid email');

    try {
      const res = await fetch(`http://localhost:8080/user/${editUser.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(formData),
      });
      if (!res.ok) throw new Error();
      toast.success('User updated');
      setEditUser(null);
      fetchUsers();
    } catch {
      toast.error('Error updating user');
    }
  };

  const handleDeleteUser = async (id) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    try {
      const res = await fetch(`http://localhost:8080/user/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${user.accessToken}` },
      });
      if (!res.ok) throw new Error();
      toast.success('User deleted');
      fetchUsers();
    } catch {
      toast.error('Error deleting user');
    }
  };

  const filteredUsers = users.filter(u =>
    u.id.toString().includes(debouncedSearch.toLowerCase()) ||
    u.email.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Manage Users</Typography>
      <TextField
        sx={{
          backgroundColor: 'white',
          borderRadius: 1,
        }}
        label="Search by ID or Email"
        fullWidth
        margin="normal"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <Button variant="contained" sx={{ marginBottom: "1rem", mt: 2 }} onClick={() => setCreateOpen(true)}>
        Create New User
      </Button>

      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Role</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredUsers.map(u => (
              <TableRow key={u.id}>
                <TableCell>{u.id}</TableCell>
                <TableCell>{u.email}</TableCell>
                <TableCell>{u.role}</TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => {
                    setEditUser(u);
                    setFormData({ email: u.email, role: u.role });
                  }}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDeleteUser(u.id)}>
                    <Delete />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>

      <Dialog open={createOpen} onClose={() => setCreateOpen(false)}>
        <DialogTitle>Create User</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth margin="normal" label="Email"
            value={createForm.email}
            onChange={(e) => setCreateForm({ ...createForm, email: e.target.value })}
          />
          <TextField
            fullWidth margin="normal" label="Password" type="password"
            value={createForm.password}
            onChange={(e) => setCreateForm({ ...createForm, password: e.target.value })}
          />
          <Select
            fullWidth margin="normal"
            value={createForm.role}
            onChange={(e) => setCreateForm({ ...createForm, role: e.target.value })}
            displayEmpty
          >
            <MenuItem value="USER">USER</MenuItem>
            <MenuItem value="ADMIN">ADMIN</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreateUser}>Create</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={!!editUser} onClose={() => setEditUser(null)}>
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth margin="normal" label="Email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <Select
            fullWidth margin="normal"
            value={formData.role}
            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
            displayEmpty
          >
            <MenuItem value="USER">USER</MenuItem>
            <MenuItem value="ADMIN">ADMIN</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditUser(null)}>Cancel</Button>
          <Button variant="contained" onClick={handleUpdateUser}>Save</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminDashboard;
