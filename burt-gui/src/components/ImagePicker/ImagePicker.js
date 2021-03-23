import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Map } from 'immutable'

import './index.scss'
import Image from './Image'

class ImagePicker extends Component {
    constructor(props) {
        super(props)
        this.state = {
            picked: Map()
        }
        this.handleImageClick = this.handleImageClick.bind(this)
        this.renderImage = this.renderImage.bind(this)
    }

    handleImageClick(image) {

        let {multiple, onPick, maxPicks, onMaxPicks} = this.props
        if (multiple) {
            let newerPickedImage;
            const pickedImage = this.state.picked;

            if (pickedImage.has(image.value)) {
                newerPickedImage = pickedImage.delete(image.value);

            } else {
                newerPickedImage = pickedImage.set(image.value, image.src);
            }
            if (newerPickedImage) {

                this.setState({picked: newerPickedImage}, function () {
                    const pickedImageToArray = []

                    this.state.picked.map((image, i) => pickedImageToArray.push({src: image.src, value: i}))
                    console.log(pickedImageToArray)
                    onPick(pickedImageToArray)
                });
            }
        } else {
            let newerPickedImage;

            // pick single image
            let pickedImage = this.state.picked;
            if (pickedImage.has(image.value)) {
                pickedImage.delete(image.value);
                newerPickedImage = pickedImage;

            } else {
                pickedImage = Map();
                newerPickedImage = pickedImage.set(image.value, image.src);
            }
            this.setState({picked: newerPickedImage}, function () {
                const pickedImageToArray = []

                this.state.picked.map((image, i) => pickedImageToArray.push({src: image.src, value: i}))
                console.log(pickedImageToArray)
                onPick(pickedImageToArray[0])
            });
        }



        // const pickedImage = multiple ? this.state.picked : Map()
        // let newerPickedImage
        //
        // if (pickedImage.has(image.value)) {
        //     newerPickedImage = pickedImage.delete(image.value)
        // } else {
        //     if (typeof maxPicks === 'undefined') {
        //         newerPickedImage = pickedImage.set(image.value, image.src)
        //     } else {
        //         if (pickedImage.size < maxPicks) {
        //             newerPickedImage = pickedImage.set(image.value, image.src)
        //         } else {
        //             onMaxPicks(image)
        //         }
        //     }
        // }
        //
        // if (newerPickedImage) {
        //     this.setState({picked: newerPickedImage})
        //
        //     const pickedImageToArray = []
        //     newerPickedImage.map((image, i) => pickedImageToArray.push({src: image, value: i}))
        //
        //     onPick(multiple ? pickedImageToArray : pickedImageToArray[0])
        // }

    }
    renderImage(image, i, style, selected) {

        return (
            <div key={image.value}>
                <Image
                    src={image.src}
                    isSelected={this.state.picked.has(image.value) || (selected !=null && selected.includes(image.value))}
                    onImageClick={() => this.handleImageClick(image)}
                    key={i}
                    style={style}
                />
                <p>{image.value}</p>
            </div>
        )
    }

    render() {
        const { images, style, selected, multiple } = this.props
        return (
            <div className="image_picker">
                { images.map( (image, i) => this.renderImage(image, i, style, selected)) }
                <div className="clear"/>
            </div>
        )
    }
}

ImagePicker.propTypes = {
    images: PropTypes.array,
    multiple: PropTypes.bool,
    onPick: PropTypes.func,
    maxPicks: PropTypes.number,
    onMaxPicks: PropTypes.func,
}

export default ImagePicker