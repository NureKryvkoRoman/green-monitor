import React, { useEffect, useState } from 'react';
import { Container, Typography, CircularProgress, Alert } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import NotificationBlock from '../components/NotificationBlock';

const Notifications = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchNotifications = async () => {
    if (!user?.email || !user?.accessToken) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/notifications/user?email=${user.email}`,
        {
          headers: {
            Authorization: `Bearer ${user.accessToken}`
          }
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch: ${response.status}`);
      }

      const data = await response.json();
      setNotifications(data);
    } catch (err) {
      setError('Could not load notifications.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [user]);

  if (!user) return null;

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Notifications
      </Typography>

      {loading && <CircularProgress />}
      {error && <Alert severity="error">{error}</Alert>}

      {!loading && notifications.length === 0 && (
        <Alert severity="info">No notifications found.</Alert>
      )}

      {notifications.map((notif) => (
        <NotificationBlock
          key={notif.id}
          notification={notif}
          onMarkedRead={(id) => setNotifications((prev) =>
            prev.map(n => n.id === id ? { ...n, read: true } : n)
          )}
        />
      ))}
    </Container>
  );
};

export default Notifications;
