import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
} from '@mui/material';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AppBar position="static" color={isAdmin ? 'info' : 'success'}>
      <Toolbar>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, cursor: 'pointer' }}
          onClick={() => {
            isAdmin ? navigate('/admin') : navigate('/');
          }}
        >
          GreenMonitor {isAdmin && 'AdminPanel'}
        </Typography>

        {isAdmin && (
          <Box display="flex" gap={5}>
            <Button color="inherit" onClick={() => navigate('/admin')}>
              Admin
            </Button>
            <Button color="inherit" onClick={() => navigate('/backup')}>
              Database Backup
            </Button>
            <Button variant="contained" color="error" onClick={logout}>
              Logout
            </Button>
          </Box>
        )}

        {user && !isAdmin && (
          <Box display="flex" gap={2}>
            <Button color="inherit" onClick={() => navigate('/')}>
              Greenhouses
            </Button>
            <Button color="inherit" onClick={logout}>
              Logout
            </Button>
          </Box>
        )}

        {!user && (
          <Box display="flex" gap={2}>
            <Button color="inherit" variant="outlined" onClick={() => navigate('/login')}>
              Login
            </Button>
            <Button color="inherit" onClick={() => navigate('/register')}>
              Register
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
