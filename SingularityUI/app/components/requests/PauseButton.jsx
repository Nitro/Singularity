import React, { Component, PropTypes } from 'react';

import { Glyphicon } from 'react-bootstrap';
import OverlayTrigger from 'react-bootstrap/lib/OverlayTrigger';
import ToolTip from 'react-bootstrap/lib/Tooltip';

import { getClickComponent } from '../common/modal/ModalWrapper';

import PauseModal from './PauseModal';

const pauseTooltip = (
  <ToolTip id="pause">
    Pause
  </ToolTip>
);

export default class PauseButton extends Component {

  static propTypes = {
    requestId: PropTypes.string.isRequired,
    isScheduled: PropTypes.bool.isRequired,
    children: PropTypes.node
  };

  static defaultProps = {
    children: (
      <OverlayTrigger placement="top" id="view-pause-overlay" overlay={pauseTooltip}>
        <a>
          <Glyphicon glyph="play" />
        </a>
      </OverlayTrigger>
    )
  };

  render() {
    return (
      <span>
        {getClickComponent(this)}
        <PauseModal
          ref="modal"
          requestId={this.props.requestId}
          isScheduled={this.props.isScheduled}
        />
      </span>
    );
  }
}
