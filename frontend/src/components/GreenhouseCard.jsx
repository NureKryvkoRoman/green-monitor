import React from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  IconButton,
} from '@mui/material';
import { useNavigate } from 'react-router';
import { Delete, EnergySavingsLeaf } from '@mui/icons-material';

const GreenhouseCard = ({ greenhouse, handleDelete }) => {
  const navigate = useNavigate();

  return (
    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
      <Card
        sx={{
          height: '100%',
          transition: 'transform 0.2s, box-shadow 0.2s',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: 6,
          },
          bgcolor: "#f9fbe7"
        }}
        onClick={() => navigate(`/greenhouses/${greenhouse.id}`)}
        style={{ cursor: 'pointer' }}
      >
        <CardContent>
          <Box display="flex" alignItems="center" justifyContent="space-between">
            <Typography variant="h6" gutterBottom>
              {greenhouse.name || `Greenhouse #${greenhouse.id}`}
            </Typography>
            <EnergySavingsLeaf color="success" />
          </Box>

          <Box mt={1} textAlign="left" display="flex" flexDirection="column" gap={0.5}>
            <Typography variant="body2" color="text.secondary">
              🌿 <strong>Plant:</strong> {greenhouse.plantName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              📡 <strong>Sensors:</strong> {greenhouse.sensorCount ?? 0}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              🔔 <strong>New notifications:</strong>{' '}
              {greenhouse.unreadNotifications > 0 ? (
                <Typography component="span" color="error" fontWeight="bold">
                  {greenhouse.unreadNotifications} Unread
                </Typography>
              ) : (
                'None'
              )}
            </Typography>
          </Box>

          <Box display="flex" justifyContent="flex-end" mt={2}>
            <IconButton
              color="error"
              onClick={(e) => {
                e.stopPropagation();
                handleDelete(greenhouse.id);
              }}
            >
              <Delete />
            </IconButton>
          </Box>
        </CardContent>
      </Card>
    </Grid>
  );
};

export default GreenhouseCard;
