/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Modal from 'akvo-flow/components/reusable/Modal';
import './style.scss';

export default class WebFormShare extends React.Component {
  state = {
    modalOpen: false,
    copyToClipboard: false,
  };

  toggleModal = () => {
    const { modalOpen } = this.state;
    this.setState({ modalOpen: !modalOpen });
  };

  copyToClipboard = () => {
    navigator.clipboard.writeText(this.props.data.shareUrl);
    this.setState({ copyToClipboard: true });
  };

  openModal = () => {
    if (!this.props.data.valid) {
      return;
    }

    this.toggleModal();
    this.props.actions.getShareURL();
  };

  render() {
    const { valid, shareUrl, sharePassword } = this.props.data;
    return (
      <>
        <li>
          <a
            onClick={this.openModal}
            href="#"
            className={`previewNewSurvey ${valid ? '' : 'disabled'}`}
          >
            Share as a webform
          </a>
        </li>

        <Modal isOpen={this.state.modalOpen} toggleModal={this.toggleModal} id="form-share-modal">
          <div className="modal-header">
            <h3>Share all questions as a webform</h3>

            <i
              className="fa fa-times icon"
              onClick={this.toggleModal}
              onKeyDown={this.toggleModal}
            />
          </div>

          <div className="modal-body">
            <div className="form-link">
              {shareUrl ? (
                <>
                  <div className="link">
                    <span>{shareUrl}</span>
                    <a onClick={this.copyToClipboard} href="#">
                      Copy link
                    </a>
                  </div>
                  {this.state.copyToClipboard && <span>Copied to clipboard</span>}

                  <div className="password">
                    <span>Password: {sharePassword}</span>
                  </div>
                </>
              ) : (
                <p>Loading URL.....</p>
              )}
            </div>

            <div className="action-button">
              <button onClick={this.toggleModal} type="button" className="button">
                Done
              </button>
            </div>
          </div>
        </Modal>
      </>
    );
  }
}

WebFormShare.propTypes = {
  strings: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
};
