import React, { useState, useRef } from 'react';
import {
  Box,
  Button,
  Typography,
  LinearProgress,
  IconButton,
  Paper,
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DeleteIcon from '@mui/icons-material/Delete';
import { fileStorageService } from '../services/api';

/**
 * FileUpload Component
 * 
 * Dosya yükleme componenti.
 * 
 * @param {function} onUpload - Dosya yüklendiğinde çağrılacak fonksiyon
 * @param {array} acceptedTypes - Kabul edilen dosya tipleri (örn: ['image/*', '.pdf'])
 * @param {number} maxSize - Maksimum dosya boyutu (MB)
 * @param {boolean} multiple - Birden fazla dosya seçilebilir mi
 */
export default function FileUpload({
  onUpload,
  acceptedTypes = ['image/*'],
  maxSize = 10,
  multiple = false,
}) {
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploadProgress, setUploadProgress] = useState({});
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    const validFiles = files.filter((file) => {
      const sizeInMB = file.size / (1024 * 1024);
      if (sizeInMB > maxSize) {
        alert(`Dosya boyutu ${maxSize} MB'dan büyük olamaz: ${file.name}`);
        return false;
      }
      return true;
    });

    if (multiple) {
      setSelectedFiles([...selectedFiles, ...validFiles]);
    } else {
      setSelectedFiles(validFiles);
    }
  };

  const handleRemoveFile = (index) => {
    setSelectedFiles(selectedFiles.filter((_, i) => i !== index));
  };

  const handleUpload = async () => {
    if (selectedFiles.length === 0) return;

    setIsUploading(true);
    const progress = {};

    try {
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        const formData = new FormData();
        formData.append('file', file);

        // TODO: File upload API entegrasyonu
        // const response = await fileStorageService.upload(formData, {
        //   onUploadProgress: (progressEvent) => {
        //     const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        //     progress[file.name] = percentCompleted;
        //     setUploadProgress({ ...progress });
        //   },
        // });

        // Simüle edilmiş upload
        await new Promise((resolve) => setTimeout(resolve, 1000));
        progress[file.name] = 100;
        setUploadProgress({ ...progress });

        if (onUpload) {
          // onUpload(response.data);
        }
      }

      setSelectedFiles([]);
      setUploadProgress({});
    } catch (error) {
      console.error('Upload error:', error);
      alert('Dosya yüklenirken bir hata oluştu');
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <Box>
      <input
        ref={fileInputRef}
        type="file"
        accept={acceptedTypes.join(',')}
        multiple={multiple}
        style={{ display: 'none' }}
        onChange={handleFileSelect}
      />

      <Button
        variant="outlined"
        component="label"
        startIcon={<CloudUploadIcon />}
        onClick={() => fileInputRef.current?.click()}
        sx={{ mb: 2 }}
      >
        Dosya Seç
      </Button>

      {selectedFiles.length > 0 && (
        <Box sx={{ mt: 2 }}>
          {selectedFiles.map((file, index) => (
            <Paper key={index} sx={{ p: 2, mb: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Box sx={{ flex: 1 }}>
                <Typography variant="body2">{file.name}</Typography>
                <Typography variant="caption" color="text.secondary">
                  {(file.size / (1024 * 1024)).toFixed(2)} MB
                </Typography>
                {uploadProgress[file.name] !== undefined && (
                  <LinearProgress
                    variant="determinate"
                    value={uploadProgress[file.name]}
                    sx={{ mt: 1 }}
                  />
                )}
              </Box>
              <IconButton size="small" onClick={() => handleRemoveFile(index)} disabled={isUploading}>
                <DeleteIcon />
              </IconButton>
            </Paper>
          ))}

          <Button
            variant="contained"
            onClick={handleUpload}
            disabled={isUploading}
            sx={{ mt: 2 }}
          >
            {isUploading ? 'Yükleniyor...' : 'Yükle'}
          </Button>
        </Box>
      )}
    </Box>
  );
}

