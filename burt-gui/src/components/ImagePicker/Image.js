import React, { Component } from 'react'
import PropTypes from 'prop-types'

export default class Image extends Component {
    constructor(props) {
        super(props)
    }



    render() {
        const { src, isSelected, onImageClick, style, disabled} = this.props
        return (
            <div
                className={`responsive${isSelected ? " selected" : ""}`}
                 onClick={() =>{
                     if (!disabled){
                         onImageClick()}
                 }}
                 >
                <img src={src}
                     className={`thumbnail${isSelected ? " selected" : ""}`}
                     style={style}
                />
                <div className="checked">
                    {/*<img src={imgCheck} style={{ width: 75, height: 75, objectFit: "cover" }}/>*/}
                    <div className="icon"/>
                </div>
                <div className="disabled">

                </div>
            </div>
        )
    }
}

Image.propTypes = {
    src: PropTypes.string,
    isSelected: PropTypes.bool
}