import React, { useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  Stack,
  Button,
  Tooltip
} from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import DoneIcon from '@mui/icons-material/Done';
import { useAuth } from '../context/AuthContext';

const urgencyIconMap = {
  INFO: <InfoIcon color="info" />,
  WARNING: <WarningIcon color="warning" />,
  CRITICAL: <ErrorIcon color="error" />
};

const urgencyColorMap = {
  INFO: 'info',
  WARNING: 'warning',
  CRITICAL: 'error'
};

const formatDate = (timestamp) => {
  const date = new Date(timestamp);
  return date.toLocaleString();
};

const NotificationBlock = ({ notification, onMarkedRead }) => {
  const { user } = useAuth();
  const [isMarking, setIsMarking] = useState(false);
  const [isRead, setIsRead] = useState(notification.read);

  const handleMarkAsRead = async () => {
    setIsMarking(true);
    try {
      const res = await fetch(
        `http://localhost:8080/api/notifications/mark-read/${notification.id}`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${user.accessToken}`
          }
        }
      );

      if (!res.ok) throw new Error('Failed to mark as read');

      setIsRead(true);
      if (onMarkedRead) onMarkedRead(notification.id);
    } catch (err) {
      console.error(err);
    } finally {
      setIsMarking(false);
    }
  };

  return (
    <Card
      variant="outlined"
      sx={{
        mb: 2,
        borderLeft: `6px solid`,
        borderColor: urgencyColorMap[notification.notificationUrgency] || 'grey.400',
        backgroundColor: isRead ? 'grey.50' : 'white'
      }}
    >
      <CardContent>
        <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}>
          <Box display="flex" alignItems="center" gap={1}>
            {urgencyIconMap[notification.notificationUrgency] || <InfoIcon />}
            <Typography variant="h6" sx={{ fontWeight: isRead ? 'normal' : 'bold' }}>
              {notification.message}
            </Typography>
          </Box>
          <Stack direction="row" spacing={1}>
            <Chip
              label={isRead ? 'Read' : 'Unread'}
              color={isRead ? 'default' : 'primary'}
              size="small"
              icon={isRead ? <DoneIcon /> : null}
            />
            {!isRead && (
              <Tooltip title="Mark as Read">
                <Button
                  variant="outlined"
                  color="success"
                  size="small"
                  onClick={handleMarkAsRead}
                  disabled={isMarking}
                >
                  Mark Read
                </Button>
              </Tooltip>
            )}
          </Stack>
        </Stack>

        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          {formatDate(notification.timestamp)}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default NotificationBlock;
