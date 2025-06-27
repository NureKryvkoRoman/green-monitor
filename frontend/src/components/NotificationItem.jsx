import React from 'react';
import {
  Paper,
  Typography,
  Box,
  Chip,
  Button,
  Stack,
} from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';

const urgencyColors = {
  INFO: 'default',
  WARNING: 'warning',
  CRITICAL: 'error',
};

const NotificationItem = ({ notification, markAsRead, greenhouseName }) => {
  const { id, message, timestamp, notificationUrgency, read } = notification;

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 2,
        mb: 2,
        bgcolor: read ? 'grey.100' : 'white',
        borderLeft: read ? '6px solid grey' : '6px solid #1976d2',
      }}
    >
      <Stack direction="row" justifyContent="space-between" alignItems="center">
        <Box>
          <Typography variant="body1" fontWeight={read ? 'normal' : 'bold'}>{message}</Typography>
          {greenhouseName && (
            <Typography variant="subtitle2" color="textSecondary">
              Greenhouse: {greenhouseName}
            </Typography>
          )}
          <Typography variant="caption" color="textSecondary">
            {new Date(timestamp).toLocaleString()}
          </Typography>
          <Box mt={1}>
            <Chip
              label={notificationUrgency}
              color={urgencyColors[notificationUrgency]}
              size="small"
            />
          </Box>
        </Box>

        {!read && (
          <Button
            variant="contained"
            size="small"
            startIcon={<CheckIcon />}
            onClick={() => markAsRead(id)}
          >
            Mark as Read
          </Button>
        )}
      </Stack>
    </Paper>
  );
};

export default NotificationItem;
