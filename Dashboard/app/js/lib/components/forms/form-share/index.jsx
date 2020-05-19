/* eslint-disable import/no-unresolved */
import React from 'react';
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
    navigator.clipboard.writeText('webform url');
    this.setState({ copyToClipboard: true });
  };

  render() {
    return (
      <>
        <li>
          <a onClick={this.toggleModal} href="#" className="previewNewSurvey">
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
              <a onClick={this.copyToClipboard} href="#">
                Copy link
              </a>
              {this.state.copyToClipboard && <span>Copied to clipboard</span>}
              <p>
                Password: <span>Webform*</span>
              </p>
            </div>

            <div className="action-button">
              <button type="button" className="button">
                Done
              </button>
            </div>
          </div>
        </Modal>
      </>
    );
  }
}
