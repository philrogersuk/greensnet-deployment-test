import React from 'react';
import styled from 'styled-components';
import ReactDOM from 'react-dom';

const Link = styled.a`
  display: block;
  color: #006937;
  border-color: #006937;
  border-style: solid;
  border-width: 1px;
  cursor: pointer;
  font-weight: bold;
  text-align: center;
  padding: 5px;
  &:hover {
    background-color: #eeeeee;
  }
`;

const MoreDetailsLink = (url: string,
                         displayText: string) => {
    return (
        <Link href={url}>{displayText}</Link>
    );
}

export const init = (element: Element,
                     url: string,
                     displayText: string) => {
    ReactDOM.render(MoreDetailsLink(url, displayText), element);
}