import React from 'react';

export default class OfflineIndicator extends React.Component {
  state = {
    isOffline: false,
  };

  componentDidMount() {
    this.timer = setInterval(() => {
      fetch('https://www.google.com/generate_204', { mode: 'no-cors' })
        .then(() => {
          this.setState({ isOffline: false });
        })
        .catch(() => {
          this.setState({ isOffline: true });
        });
    }, 5000);
  }

  componentWillUnmount() {
    clearInterval(this.timer);
  }

  render() {
    return this.state.isOffline && <p>You are offline. Changes {"won't"} be saved</p>;
  }
}
