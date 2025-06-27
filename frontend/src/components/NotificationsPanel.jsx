import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  CircularProgress,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Stack,
} from '@mui/material';
import { toast } from 'react-toastify';
import NotificationItem from './NotificationItem';

const NotificationsPanel = ({ fetchUrl, token, greenhousesList = [] }) => {
  const [notifications, setNotifications] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [urgencyFilter, setUrgencyFilter] = useState('ALL');
  const [readFilter, setReadFilter] = useState('ALL');

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const res = await fetch(fetchUrl, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log(res)
        if (!res.ok) throw new Error('Failed to fetch notifications');

        const data = await res.json();
        setNotifications(data);
        setFiltered(data);
      } catch (err) {
        console.error(err);
        toast.error('Error loading notifications');
      } finally {
        setLoading(false);
      }
    };

    if (token) fetchNotifications();
  }, [fetchUrl, token]);

  useEffect(() => {
    let result = [...notifications];

    if (urgencyFilter !== 'ALL') {
      result = result.filter((n) => n.notificationUrgency === urgencyFilter);
    }

    if (readFilter !== 'ALL') {
      result = result.filter((n) => n.read === (readFilter === 'READ'));
    }

    setFiltered(result);
  }, [urgencyFilter, readFilter, notifications]);

  const markAsRead = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/notifications/mark-read/${id}`, {
        method: 'PATCH',
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error('Failed to mark as read');

      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch (err) {
      console.error(err);
      toast.error('Error marking notification as read');
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box mt={4}>
      <Typography variant="h5" gutterBottom>
        Notifications
      </Typography>

      <Stack direction="row" spacing={2} mb={2}>
        <FormControl size="small">
          <InputLabel>Urgency</InputLabel>
          <Select
            value={urgencyFilter}
            label="Urgency"
            onChange={(e) => setUrgencyFilter(e.target.value)}
          >
            <MenuItem value="ALL">All</MenuItem>
            <MenuItem value="INFO">Info</MenuItem>
            <MenuItem value="WARNING">Warning</MenuItem>
            <MenuItem value="CRITICAL">Critical</MenuItem>
          </Select>
        </FormControl>

        <FormControl size="small">
          <InputLabel>Status</InputLabel>
          <Select
            value={readFilter}
            label="Status"
            onChange={(e) => setReadFilter(e.target.value)}
          >
            <MenuItem value="ALL">All</MenuItem>
            <MenuItem value="UNREAD">Unread</MenuItem>
            <MenuItem value="READ">Read</MenuItem>
          </Select>
        </FormControl>
      </Stack>

      {filtered.length === 0 ? (
        <Typography>No notifications found.</Typography>
      ) : (
        filtered.map((n) => (
          <NotificationItem
            key={n.id}
            notification={n}
            markAsRead={markAsRead}
            greenhouseName={greenhousesList.find(gh => gh.id == n.greenhouseId)?.name}
          />
        ))
      )}
    </Box>
  );
};

export default NotificationsPanel;
