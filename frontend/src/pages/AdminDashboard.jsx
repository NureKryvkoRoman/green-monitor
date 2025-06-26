import React, { useEffect, useState } from 'react';
import {
  Container, Typography, Paper, Table, TableHead, TableRow, TableCell, TableBody,
  IconButton, Dialog, DialogTitle, DialogContent, TextField, DialogActions, Button
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router';

const AdminDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [editUser, setEditUser] = useState(null);
  const [formData, setFormData] = useState({ login: '', email: '', role: 'USER' });
  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState({
    login: '',
    email: '',
    password: '',
    role: 'USER',
  });

  const fetchUsers = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/user', {
        headers: { Authorization: `Bearer ${user.accessToken}` },
      });
      if (!res.ok) throw new Error('Failed to load users');
      const data = await res.json();
      console.log(data)
      setUsers(data);
    } catch (err) {
      toast.error('Error fetching users');
    }
  };

  useEffect(() => {
    if (user?.role !== 'ADMIN') {
      navigate('/login');
    }
    fetchUsers();
  }, [user]);

  const handleEdit = (u) => {
    setEditUser(u);
    setFormData({ login: u.username, email: u.email, role: u.role });
  };

  const isEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleCreateUser = async () => {
    if (!isEmail(createForm.email)) {
      toast.error('Please enter a valid email address');
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/user`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify(createForm),
      });
      if (!res.ok) throw new Error('User creation failed');
      toast.success('User created');
      setCreateOpen(false);
      setCreateForm({ username: '', email: '', password: '', role: 'USER' });
      fetchUsers();
    } catch (err) {
      toast.error('Error creating user');
    }
  };

  const handleUpdate = async () => {
    if (!isEmail(formData.email)) {
      toast.error('Please enter a valid email address');
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/user/${editUser.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`,
        },
        body: JSON.stringify({ ...editUser, ...formData }),
      });
      if (!res.ok) throw new Error('Update failed');
      toast.success('User updated');
      setEditUser(null);
      fetchUsers();
    } catch (err) {
      toast.error('Error updating user');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    try {
      const res = await fetch(`http://localhost:8080/api/user/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.accessToken}`,
        },
      });
      if (!res.ok) throw new Error('Delete failed');
      toast.success('User deleted');
      fetchUsers();
    } catch (err) {
      toast.error('Error deleting user');
    }
  };

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Admin Dashboard</Typography>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>User ID</TableCell>
              <TableCell>Username</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Role</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map(u => (
              <TableRow key={u.id}>
                <TableCell>{u.id}</TableCell>
                <TableCell>{u.username}</TableCell>
                <TableCell>{u.email}</TableCell>
                <TableCell>{u.role}</TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => handleEdit(u)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(u.id)}>
                    <Delete />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>

      <Button variant="contained" sx={{ mt: 2 }} onClick={() => setCreateOpen(true)}>
        Create New User
      </Button>

      <Dialog open={createOpen} onClose={() => setCreateOpen(false)}>
        <DialogTitle>Create User</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth margin="normal" label="Username"
            value={createForm.login}
            onChange={(e) => setCreateForm({ ...createForm, login: e.target.value })}
          />
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
          <TextField
            fullWidth margin="normal" label="Role"
            select SelectProps={{ native: true }}
            value={createForm.role}
            onChange={(e) => setCreateForm({ ...createForm, role: e.target.value })}
          >
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </TextField>
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
            fullWidth margin="normal" label="Username"
            value={formData.login}
            onChange={(e) => setFormData({ ...formData, login: e.target.value })}
          />
          <TextField
            fullWidth margin="normal" label="Email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <TextField
            fullWidth margin="normal" label="Role"
            select
            slotProps={{ select: { native: true } }}
            value={formData.role}
            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
          >
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditUser(null)}>Cancel</Button>
          <Button variant="contained" onClick={handleUpdate}>Save</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminDashboard;
