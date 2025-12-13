import React from 'react';
import {
  Box,
  Skeleton as MuiSkeleton,
  Card,
  CardContent,
  Grid,
} from '@mui/material';

/**
 * Skeleton Components
 * 
 * Farklı içerik türleri için hazır skeleton componentleri.
 */

// Genel Skeleton
export function Skeleton({ variant = 'text', ...props }) {
  return <MuiSkeleton variant={variant} {...props} />;
}

// Hospital Card Skeleton
export function HospitalCardSkeleton() {
  return (
    <Card>
      <MuiSkeleton variant="rectangular" height={180} />
      <CardContent>
        <MuiSkeleton variant="text" width="80%" height={32} />
        <MuiSkeleton variant="text" width="60%" height={24} sx={{ mt: 1 }} />
        <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
          <MuiSkeleton variant="rectangular" width={60} height={24} />
          <MuiSkeleton variant="rectangular" width={60} height={24} />
        </Box>
        <MuiSkeleton variant="text" width="40%" height={20} sx={{ mt: 2 }} />
      </CardContent>
    </Card>
  );
}

// Doctor Card Skeleton
export function DoctorCardSkeleton() {
  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <MuiSkeleton variant="circular" width={80} height={80} />
          <Box sx={{ flex: 1 }}>
            <MuiSkeleton variant="text" width="70%" height={28} />
            <MuiSkeleton variant="text" width="50%" height={20} sx={{ mt: 1 }} />
            <MuiSkeleton variant="text" width="60%" height={20} sx={{ mt: 1 }} />
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}

// Grid Skeleton
export function GridSkeleton({ items = 6, columns = 3 }) {
  return (
    <Grid container spacing={3}>
      {Array.from({ length: items }).map((_, index) => (
        <Grid item xs={12} sm={6} md={12 / columns} key={index}>
          <MuiSkeleton variant="rectangular" height={200} />
        </Grid>
      ))}
    </Grid>
  );
}

// Table Skeleton
export function TableSkeleton({ rows = 5, columns = 4 }) {
  return (
    <Box>
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <Box
          key={rowIndex}
          sx={{ display: 'flex', gap: 2, mb: 2 }}
        >
          {Array.from({ length: columns }).map((_, colIndex) => (
            <MuiSkeleton
              key={colIndex}
              variant="text"
              width="100%"
              height={40}
            />
          ))}
        </Box>
      ))}
    </Box>
  );
}

// List Skeleton
export function ListSkeleton({ items = 5 }) {
  return (
    <Box>
      {Array.from({ length: items }).map((_, index) => (
        <Box
          key={index}
          sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'center' }}
        >
          <MuiSkeleton variant="circular" width={40} height={40} />
          <Box sx={{ flex: 1 }}>
            <MuiSkeleton variant="text" width="60%" height={24} />
            <MuiSkeleton variant="text" width="40%" height={20} sx={{ mt: 0.5 }} />
          </Box>
        </Box>
      ))}
    </Box>
  );
}

export default Skeleton;

