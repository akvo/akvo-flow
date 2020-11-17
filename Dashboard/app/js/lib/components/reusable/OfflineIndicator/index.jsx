import React from 'react';

export default class OfflineIndicator extends React.Component {
  state = {
    isOffline: false,
  };

  componentDidMount() {
    window.addEventListener('online', this.onlineStatus);
    window.addEventListener('offline', this.onlineStatus);
  }

  componentWillUnmount() {
    window.removeEventListener('online', this.onlineStatus);
    window.removeEventListener('offline', this.onlineStatus);
  }

  onlineStatus = () => {
    this.setState({ isOffline: !navigator.onLine });
  };

  render() {
    return this.state.isOffline && <p>You are offline. Changes {"won't"} be saved</p>;
  }
}
