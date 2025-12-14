import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, ActivityIndicator } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export default function ReservationsScreen({ navigation }) {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const response = await axios.get(`${API_URL}/reservations`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setReservations(response.data);
    } catch (error) {
      console.error('Error fetching reservations:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#1976d2" />
      </View>
    );
  }

  if (reservations.length === 0) {
    return (
      <View style={styles.center}>
        <Icon name="event-busy" size={60} color="#999" />
        <Text style={styles.emptyText}>Henüz rezervasyonunuz yok</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={reservations}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.reservationCard}>
            <Icon name="event" size={30} color="#1976d2" />
            <View style={styles.reservationInfo}>
              <Text style={styles.reservationTitle}>{item.hospitalName}</Text>
              <Text style={styles.reservationDate}>
                <Icon name="calendar-today" size={16} color="#666" /> {item.date}
              </Text>
              <Text style={styles.reservationTime}>
                <Icon name="access-time" size={16} color="#666" /> {item.time}
              </Text>
              <Text style={styles.reservationStatus}>
                Durum: {item.status}
              </Text>
            </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
    marginTop: 10,
  },
  reservationCard: {
    backgroundColor: 'white',
    flexDirection: 'row',
    padding: 15,
    margin: 10,
    borderRadius: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  reservationInfo: {
    flex: 1,
    marginLeft: 15,
  },
  reservationTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  reservationDate: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  reservationTime: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  reservationStatus: {
    fontSize: 14,
    color: '#1976d2',
    marginTop: 5,
    fontWeight: 'bold',
  },
});

