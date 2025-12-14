import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

export default function HomeScreen({ navigation }) {
  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Health Tourism</Text>
        <Text style={styles.subtitle}>İstanbul Sağlık Turizmi</Text>
      </View>

      <View style={styles.cards}>
        <TouchableOpacity 
          style={styles.card}
          onPress={() => navigation.navigate('Hospitals')}
        >
          <Icon name="local-hospital" size={40} color="#1976d2" />
          <Text style={styles.cardTitle}>Hastaneler</Text>
          <Text style={styles.cardText}>En iyi hastaneleri keşfedin</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={styles.card}
          onPress={() => navigation.navigate('Doctors')}
        >
          <Icon name="person" size={40} color="#1976d2" />
          <Text style={styles.cardTitle}>Doktorlar</Text>
          <Text style={styles.cardText}>Uzman doktorları bulun</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={styles.card}
          onPress={() => navigation.navigate('Reservations')}
        >
          <Icon name="event" size={40} color="#1976d2" />
          <Text style={styles.cardTitle}>Rezervasyonlarım</Text>
          <Text style={styles.cardText}>Randevularınızı görüntüleyin</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#1976d2',
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: 'white',
  },
  subtitle: {
    fontSize: 16,
    color: 'white',
    marginTop: 5,
  },
  cards: {
    padding: 15,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 20,
    marginBottom: 15,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 10,
    color: '#333',
  },
  cardText: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
    textAlign: 'center',
  },
});

