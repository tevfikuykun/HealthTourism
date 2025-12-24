/**
 * WebRTC Utility Functions
 * Handles peer-to-peer video connections
 */

const DEFAULT_STUN_SERVER = 'stun:stun.l.google.com:19302';

export class WebRTCManager {
  constructor(roomId, userId, apiBaseUrl = '/api/telemedicine/webrtc') {
    this.roomId = roomId;
    this.userId = userId;
    this.apiBaseUrl = apiBaseUrl;
    this.localStream = null;
    this.remoteStream = null;
    this.peerConnection = null;
    this.offerId = null;
    this.isInitiator = false;
  }

  /**
   * Initialize WebRTC connection
   */
  async initialize(isInitiator = false) {
    this.isInitiator = isInitiator;
    
    // Get ICE servers
    const iceServers = await this.getIceServers();
    
    // Create RTCPeerConnection
    this.peerConnection = new RTCPeerConnection({
      iceServers: iceServers
    });

    // Handle ICE candidates
    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendIceCandidate(event.candidate);
      }
    };

    // Handle remote stream
    this.peerConnection.ontrack = (event) => {
      this.remoteStream = event.streams[0];
      if (this.onRemoteStream) {
        this.onRemoteStream(this.remoteStream);
      }
    };

    // Handle connection state changes
    this.peerConnection.onconnectionstatechange = () => {
      if (this.onConnectionStateChange) {
        this.onConnectionStateChange(this.peerConnection.connectionState);
      }
    };
  }

  /**
   * Get user media (camera and microphone)
   */
  async getUserMedia() {
    try {
      this.localStream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true
      });
      
      // Add local stream to peer connection
      this.localStream.getTracks().forEach(track => {
        this.peerConnection.addTrack(track, this.localStream);
      });
      
      return this.localStream;
    } catch (error) {
      console.error('Error getting user media:', error);
      throw error;
    }
  }

  /**
   * Create offer (initiator)
   */
  async createOffer() {
    try {
      const offer = await this.peerConnection.createOffer();
      await this.peerConnection.setLocalDescription(offer);

      // Send offer to signaling server
      const response = await fetch(`${this.apiBaseUrl}/offer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          roomId: this.roomId,
          userId: this.userId,
          offer: offer
        })
      });

      const data = await response.json();
      this.offerId = data.offerId;
      
      return offer;
    } catch (error) {
      console.error('Error creating offer:', error);
      throw error;
    }
  }

  /**
   * Create answer (receiver)
   */
  async createAnswer(offerId, remoteOffer) {
    try {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(remoteOffer));
      const answer = await this.peerConnection.createAnswer();
      await this.peerConnection.setLocalDescription(answer);

      // Send answer to signaling server
      await fetch(`${this.apiBaseUrl}/answer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          offerId: offerId,
          userId: this.userId,
          answer: answer
        })
      });

      return answer;
    } catch (error) {
      console.error('Error creating answer:', error);
      throw error;
    }
  }

  /**
   * Set remote description (for initiator)
   */
  async setRemoteAnswer(answer) {
    try {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
    } catch (error) {
      console.error('Error setting remote answer:', error);
      throw error;
    }
  }

  /**
   * Send ICE candidate
   */
  async sendIceCandidate(candidate) {
    try {
      await fetch(`${this.apiBaseUrl}/ice-candidate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          offerId: this.offerId,
          candidate: {
            candidate: candidate.candidate,
            sdpMLineIndex: candidate.sdpMLineIndex,
            sdpMid: candidate.sdpMid
          }
        })
      });
    } catch (error) {
      console.error('Error sending ICE candidate:', error);
    }
  }

  /**
   * Add remote ICE candidate
   */
  async addRemoteIceCandidate(candidate) {
    try {
      await this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    } catch (error) {
      console.error('Error adding remote ICE candidate:', error);
    }
  }

  /**
   * Get ICE servers configuration
   */
  async getIceServers() {
    try {
      const response = await fetch(`${this.apiBaseUrl}/ice-servers`);
      const data = await response.json();
      return data.iceServers || [{ urls: DEFAULT_STUN_SERVER }];
    } catch (error) {
      console.error('Error getting ICE servers:', error);
      return [{ urls: DEFAULT_STUN_SERVER }];
    }
  }

  /**
   * Toggle mute audio
   */
  toggleMute() {
    if (this.localStream) {
      this.localStream.getAudioTracks().forEach(track => {
        track.enabled = !track.enabled;
      });
    }
  }

  /**
   * Toggle video
   */
  toggleVideo() {
    if (this.localStream) {
      this.localStream.getVideoTracks().forEach(track => {
        track.enabled = !track.enabled;
      });
    }
  }

  /**
   * Close connection
   */
  async close() {
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
    }
    
    if (this.peerConnection) {
      this.peerConnection.close();
    }

    if (this.offerId) {
      await fetch(`${this.apiBaseUrl}/close/${this.offerId}`, {
        method: 'POST'
      });
    }
  }
}

export default WebRTCManager;

