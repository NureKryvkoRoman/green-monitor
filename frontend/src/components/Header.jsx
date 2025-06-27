import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
} from '@mui/material';
import {
  EnergySavingsLeafSharp,
  Grass,
  GrassSharp,
  Home,
  People
} from '@mui/icons-material';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AppBar
      position="static"
      sx={{
        background: isAdmin
          ? 'linear-gradient(to right, #0288d1, #26c6da)'
          : 'linear-gradient(to right, #2e7d32, #66bb6a)',
        boxShadow: 3,
      }}
    >
      <Toolbar>
        <Box
          display="flex"
          alignItems="center"
          sx={{ flexGrow: 1 }}
        >
          <EnergySavingsLeafSharp sx={{ mr: 1 }} />
          <Typography
            variant="h6"
            fontWeight="bold"
            onClick={() => navigate(isAdmin ? '/admin' : '/')}
            sx={{ cursor: 'pointer' }}
          >
            GreenMonitor {isAdmin && 'AdminPanel'}
          </Typography>
        </Box>

        {isAdmin && (
          <Box display="flex" gap={2} alignItems="center">
            <Button
              startIcon={<People />}
              variant="outlined"
              color="inherit"
              onClick={() => navigate('/admin')}
            >
              Manage users
            </Button>
            <Button
              startIcon={<GrassSharp />}
              variant="outlined"
              color="inherit"
              onClick={() => navigate('/admin/plants')}
            >
              Manage plants
            </Button>
            <Button variant="contained" color="error" onClick={() => { logout(); navigate('/') }}>
              Logout
            </Button>
          </Box>
        )}

        {user && !isAdmin && (
          <Box display="flex" gap={2}>
            <Button
              startIcon={<Grass />}
              sx={{
                bgcolor: "#2e7d32",
                ":hover": {
                  bgcolor: "#296e2c",
                  boxShadow: 3
                }
              }}
              color="inherit"
              onClick={() => navigate('/plants')}>
              Plants
            </Button>
            <Button
              startIcon={<Home />}
              sx={{
                bgcolor: "#2e7d32",
                ":hover": {
                  bgcolor: "#296e2c",
                  boxShadow: 3
                }
              }}
              color="inherit"
              onClick={() => navigate('/')}>
              My greenhouses
            </Button>
            <Button variant="contained" color="error" onClick={() => { logout(); navigate('/') }}>
              Logout
            </Button>
          </Box>
        )}

        {!user && (
          <Box display="flex" gap={2}>
            <Button
              startIcon={<Grass />}
              sx={{
                bgcolor: "#2e7d32",
                ":hover": {
                  bgcolor: "#296e2c",
                  boxShadow: 3
                }
              }}
              color="inherit"
              onClick={() => navigate('/plants')}>
              Plants
            </Button>
            <Button color="inherit" variant="outlined" onClick={() => navigate('/login')}>
              Login
            </Button>
            <Button color="inherit" onClick={() => navigate('/register')}>
              Register
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar >
  );
};

export default Header;
