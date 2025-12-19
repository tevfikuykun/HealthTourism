import React, { useState } from 'react';
import {
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Chip,
  Tooltip,
} from '@mui/material';
import {
  AccountTree,
  Business,
  LocalHospital,
  CheckCircle,
} from '@mui/icons-material';
import { useTranslation } from '../../i18n';

/**
 * Tenant Switcher Component
 * Multi-Tenancy için organizasyon değiştirici
 */
const TenantSwitcher = ({ tenants, selectedTenant, onTenantChange }) => {
  const { t } = useTranslation();
  const [anchorEl, setAnchorEl] = useState(null);

  const handleOpen = (event) => setAnchorEl(event.currentTarget);
  const handleClose = () => setAnchorEl(null);

  const handleSelect = (tenant) => {
    onTenantChange(tenant);
    handleClose();
  };

  if (!tenants || tenants.length <= 1) {
    return selectedTenant ? (
      <Chip
        icon={<AccountTree />}
        label={selectedTenant.name}
        size="small"
        variant="outlined"
      />
    ) : null;
  }

  const getTenantIcon = (type) => {
    switch (type) {
      case 'HOSPITAL':
        return <LocalHospital />;
      case 'CLINIC':
        return <Business />;
      default:
        return <AccountTree />;
    }
  };

  return (
    <>
      <Tooltip title={t('header.switchOrganization')}>
        <IconButton onClick={handleOpen} size="small">
          <AccountTree />
        </IconButton>
      </Tooltip>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          sx: { minWidth: 250 },
        }}
      >
        <MenuItem disabled>
          <ListItemText
            primary={t('header.selectOrganization')}
            primaryTypographyProps={{ variant: 'caption', color: 'text.secondary' }}
          />
        </MenuItem>
        {tenants.map((tenant) => (
          <MenuItem
            key={tenant.id}
            selected={selectedTenant?.id === tenant.id}
            onClick={() => handleSelect(tenant)}
          >
            <ListItemIcon>{getTenantIcon(tenant.type)}</ListItemIcon>
            <ListItemText
              primary={tenant.name}
              secondary={tenant.type}
            />
            {selectedTenant?.id === tenant.id && (
              <CheckCircle color="primary" fontSize="small" />
            )}
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default TenantSwitcher;


