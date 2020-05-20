import React from 'react';
import types from 'prop-types';
import ReactModal from 'react-modal';
import './style.scss';

export default class Modal extends React.Component {
  render() {
    return (
      <ReactModal
        isOpen={this.props.isOpen}
        onRequestClose={this.props.toggleModal}
        id={this.props.id}
        ariaHideApp={false}
        overlayClassName="modal-overlay"
        className="modal-container"
      >
        {this.props.children}
      </ReactModal>
    );
  }
}

Modal.propTypes = {
  isOpen: types.bool.isRequired,
  toggleModal: types.func.isRequired,
  id: types.string.isRequired,
  children: types.any.isRequired,
};
