import React, { useEffect, useState } from 'react';
import NotificationsIcon from '@mui/icons-material/Notifications'
import {
  Container, TextField, Button, Typography, Paper, Box,
  CircularProgress, Dialog, DialogTitle, DialogContent, IconButton
} from '@mui/material';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import Notifications from './Notifications.jsx'

const UserProfile = () => {
  const { user } = useAuth();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: ''
  });

  const fetchProfile = async () => {
    if (!user?.accessToken || !user?.email) return;

    setLoading(true);
    try {
      const res = await fetch(`http://localhost:8080/api/userinfo/email?email=${encodeURIComponent(user.email)}`, {
        headers: {
          Authorization: `Bearer ${user.accessToken}`
        }
      });

      if (res.ok) {
        const data = await res.json();
        setProfile(data);
        setFormData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          phoneNumber: data.phoneNumber || ''
        });
      } else {
        toast.info("Profile not found. You can create one.");
        setProfile(null);
      }
    } catch (err) {
      toast.error("Failed to load profile.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSave = async () => {
    const body = JSON.stringify({
      ...formData,
      user: { id: user.id }
    });

    try {
      let url, method;
      if (profile) {
        url = `http://localhost:8080/api/userinfo/${profile.id}`;
        method = 'PATCH'
      } else {
        url = 'http://localhost:8080/api/userinfo';
        method = 'POST'
      }

      const res = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.accessToken}`
        },
        body
      });

      if (!res.ok) {
        toast.error("Failed to save profile.");
        console.log(res)
      }

      toast.success(profile ? "Profile updated successfully." : "Profile created successfully.");
      setEditing(false);
      fetchProfile();
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, [user]);

  if (!user) return null;

  if (loading) {
    return (
      <Container sx={{ mt: 8, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Paper sx={{ p: 5, mt: 6 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h4" gutterBottom>User Profile</Typography>
          <IconButton color="primary" onClick={() => setNotificationsOpen(true)}>
            <NotificationsIcon />
          </IconButton>
        </Box>

        <Box component="form" noValidate autoComplete="off" sx={{ mt: 2 }}>
          <TextField
            label="First Name"
            name="firstName"
            fullWidth
            margin="normal"
            value={formData.firstName}
            onChange={handleChange}
            disabled={!editing}
          />
          <TextField
            label="Last Name"
            name="lastName"
            fullWidth
            margin="normal"
            value={formData.lastName}
            onChange={handleChange}
            disabled={!editing}
          />
          <TextField
            label="Phone Number"
            name="phoneNumber"
            fullWidth
            margin="normal"
            value={formData.phoneNumber}
            onChange={handleChange}
            disabled={!editing}
          />

          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'space-between' }}>
            {editing ? (
              <>
                <Button variant="contained" color="success" onClick={handleSave}>Save</Button>
                <Button variant="outlined" color="inherit" onClick={() => setEditing(false)}>Cancel</Button>
              </>
            ) : (
              <Button variant="contained" onClick={() => setEditing(true)}>
                {profile ? 'Edit Profile' : 'Create Profile'}
              </Button>
            )}
          </Box>
        </Box>
      </Paper>

      <Dialog
        open={notificationsOpen}
        onClose={() => setNotificationsOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Notifications</DialogTitle>
        <DialogContent dividers>
          <Notifications />
        </DialogContent>
      </Dialog>
    </Container>
  );
};

export default UserProfile;
