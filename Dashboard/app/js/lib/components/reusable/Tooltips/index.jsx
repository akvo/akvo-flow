import React from 'react';
import PropTypes from 'prop-types';

const xOffset = 10;
const yOffset = 20;

// Mouse events
const mouseEnter = e => {
  const tooltipText = $(e.target).attr('data-title');
  $('body').append(`<p id='tooltip'>${tooltipText}</p>`);
  $('#tooltip')
    .css('top', `${e.pageY - xOffset}px`)
    .css('left', `${e.pageX + yOffset}px`)
    .fadeIn('fast');
};

const mouseLeave = () => {
  $('#tooltip').remove();
};

const mouseMove = e => {
  $('#tooltip')
    .css('top', `${e.pageY - xOffset}px`)
    .css('left', `${e.pageX + yOffset}px`);
};

export default function Tooltip({ dataTitle }) {
  return (
    <div
      onMouseEnter={mouseEnter}
      onMouseMove={mouseMove}
      onMouseLeave={mouseLeave}
      className="helpIcon tooltip"
      data-title={dataTitle}
    >
      ?
    </div>
  );
}

Tooltip.propTypes = {
  dataTitle: PropTypes.string.isRequired,
};
